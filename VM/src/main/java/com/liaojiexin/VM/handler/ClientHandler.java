package com.liaojiexin.VM.handler;

import com.liaojiexin.VM.client.WebSocketClient;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.ui.Model;

/**
 * @ClassName: ClientHandler
 * @Description: TODO
 * @version: 1.0
 * @author: liaojiexin
 * @date: 2021/3/17 15:34
 */
public class ClientHandler {
    private WebSocketClient client;

    public ClientHandler(WebSocketClient client) {
        this.client = client;
    }
}
