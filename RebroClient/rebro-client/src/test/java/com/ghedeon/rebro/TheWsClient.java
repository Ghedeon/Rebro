package com.ghedeon.rebro;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TheWsClient {

    @Mock
    private IWsServerLogger wsServerLogger;
    @Mock
    private IRealmManager realmManager;
    @Captor
    private ArgumentCaptor<String> captor;

    private FakeWsServer server;
    private WsClient client;

    @Before
    public void setUp() throws Exception {
        server = new FakeWsServer(wsServerLogger);
        server.start();
        final String serverIP = server.getAddress().getHostName();
        client = new WsClient(serverIP, realmManager, "fakeDeviceName");
    }

    @Test
    public void connects_to_server() throws Exception {
        // when
        client.connect();
        Thread.sleep(100);

        // then
        verify(wsServerLogger).onOpen();
    }

    @Test
    public void sends_CONNECT_command_on_connect() throws InterruptedException {
        // when
        client.connect();
        Thread.sleep(100);

        // then
        verify(wsServerLogger).onMessage(captor.capture());
        assertThat(captor.getValue()).contains(asList("\"method\":\"CONNECT\"", "\"DEVICE_NAME\":\"fakeDeviceName\""));
    }

    @Test
    public void lists_DB_on_LIST_command() throws InterruptedException {
        // given
        client.connect();
        Thread.sleep(100);

        // when
        server.send(RpcMethod.LIST);
        Thread.sleep(100);

        // then
        verify(realmManager).list();
    }

    @Test
    public void sends_response_on_LIST_command() throws InterruptedException {
        // given
        client.connect();
        Thread.sleep(100);
        reset(wsServerLogger);

        // when
        server.send(RpcMethod.LIST);
        Thread.sleep(100);

        // then
        verify(wsServerLogger).onMessage(captor.capture());
        assertThat(captor.getValue()).contains("result");
    }

    @Test
    public void pushes_all_on_PUSH_command() throws InterruptedException {
        // given
        client.connect();
        Thread.sleep(100);
        reset(wsServerLogger);

        // when
        client.pushAll();
        Thread.sleep(100);

        // then
        verify(realmManager).list();
        verify(wsServerLogger).onMessage(captor.capture());
        assertThat(captor.getValue()).contains("\"method\":\"PUSH\"");
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
        client.close();
    }

}