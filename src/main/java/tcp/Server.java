package tcp;

import com.google.protobuf.InvalidProtocolBufferException;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;

import udp.GexMessage;

public class Server extends WebSocketServer {

    public Server( int port ) {
        super( new InetSocketAddress( port ) );
    }

    public Server( InetSocketAddress address ) {
        super( address );
    }

    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " connected" );
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        System.out.println( conn + " disconnected." );
    }

    @Override
    public void onMessage( WebSocket conn, String message ) {
        System.out.println( "===================================================================================");
        System.out.println( "Text message");
        System.out.println( conn + ": " + message );
    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        try {
            GexMessage gm  = new GexMessage(message.array());
            System.out.println("Gex message:");
            System.out.println(gm);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        ex.printStackTrace();
        if( conn != null ) {
           // todo: handle errors
        }
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
    }

    public static void main( String[] args ) throws InterruptedException , IOException {
        WebSocketImpl.DEBUG = false;
        int port = 8884;
        Server s = new Server( port );
        s.start();
        System.out.println( "Server started on port: " + s.getPort() );
    }

}