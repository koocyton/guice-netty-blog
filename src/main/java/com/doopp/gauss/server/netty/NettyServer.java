package com.doopp.gauss.server.netty;

import com.doopp.gauss.server.handler.Http1RequestHandler;
import com.doopp.gauss.server.application.ApplicationProperties;
import com.doopp.gauss.server.handler.HttpStaticFileServerHandler;
import com.google.inject.Injector;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import com.google.inject.Inject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.InputStream;
import java.security.KeyStore;

public class NettyServer {

    @Inject
    private ApplicationProperties applicationProperties;

    @Inject
    private Injector injector;

    @Inject
    private EventLoopGroup bossGroup;

    @Inject
    private EventLoopGroup workerGroup;

    public void run() throws Exception {

        String host = applicationProperties.s("server.host");
        int port = applicationProperties.i("server.port");
        int sslPort = applicationProperties.i("server.sslPort");

        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            System.out.print(">>> Running ServerBootstrap on " + host + ":" + port + "/" + sslPort + "\n");

            Channel ch80 = b.bind(host, port).sync().channel();
            Channel ch443 = b.bind(host, sslPort).sync().channel();

            ch80.closeFuture().sync();
            ch443.closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    // channelInitializer
    private ChannelInitializer channelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                if (ch.localAddress().getPort() == applicationProperties.i("server.sslPort")) {
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(getKeyManagers(), null, null);
                    SSLEngine sslEngine = sslContext.createSSLEngine();
                    sslEngine.setUseClientMode(false);
                    ch.pipeline().addLast(new SslHandler(sslEngine));
                }
                // HttpServerCodec：将请求和应答消息解码为HTTP消息
                pipeline.addLast(new HttpServerCodec());
                // HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息
                pipeline.addLast(new HttpObjectAggregator(65536));
                // that adds support for writing a large data stream
                pipeline.addLast(new ChunkedWriteHandler());
                // static file
                pipeline.addLast(injector.getInstance(HttpStaticFileServerHandler.class));
                // pipeline.addLast(injector.getInstance(HttpStaticFileResourceHandler.class));
                // http request
                pipeline.addLast(injector.getInstance(Http1RequestHandler.class));
            }
        };
    }

    private KeyManager[] getKeyManagers() {
        String jksPassword = applicationProperties.s("server.jks.password");
        String jksSecret = applicationProperties.s("server.jks.secret");

        try {
            InputStream jksInputStream = applicationProperties.r("server.jks.file");
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(jksInputStream, jksPassword.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, jksSecret.toCharArray());
            jksInputStream.close();
            return keyManagerFactory.getKeyManagers();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}



