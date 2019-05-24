package cn.dubby.nw;

import cn.dubby.nw.handler.AuthHandler;
import cn.dubby.nw.handler.HttpStaticFileServerInitializer;
import cn.dubby.nw.util.RandomStringUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

/**
 * @author dubby
 * @date 2019/5/21 14:33
 */
public class Main {

    private static final InternalLogger logger;

    static {
        InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
        logger = InternalLoggerFactory.getInstance(AuthHandler.class);
    }

    private static final String PATH = System.getProperty("path", "/");

    private static final String TOKEN = System.getProperty("token", RandomStringUtil.random(100));

    private static final boolean SSL = System.getProperty("ssl") != null;

    private static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8443" : "8080"));

    public static void main(String[] args) throws Exception {
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                    .sslProvider(SslProvider.JDK).build();
        } else {
            sslCtx = null;
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(5);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpStaticFileServerInitializer(sslCtx, PATH, TOKEN));

            Channel ch = b.bind(PORT).sync().channel();

            logger.info("Open your web browser and navigate to {}://127.0.0.1:{}/", sslCtx == null ? "http" : "https", PORT);
            logger.info("token:[{}]", TOKEN);

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
