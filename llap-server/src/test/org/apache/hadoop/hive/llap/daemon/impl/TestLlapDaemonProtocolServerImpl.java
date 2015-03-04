/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hive.llap.daemon.impl;

import static org.mockito.Mockito.*;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

import com.google.protobuf.ServiceException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.llap.daemon.ContainerRunner;
import org.apache.hadoop.hive.llap.daemon.LlapDaemonConfiguration;
import org.apache.hadoop.hive.llap.daemon.LlapDaemonProtocolBlockingPB;
import org.apache.hadoop.hive.llap.daemon.rpc.LlapDaemonProtocolProtos.RunContainerRequestProto;
import org.junit.Test;

public class TestLlapDaemonProtocolServerImpl {


  @Test(timeout = 10000)
  public void test() throws ServiceException {
    LlapDaemonConfiguration daemonConf = new LlapDaemonConfiguration();
    LlapDaemonProtocolServerImpl server =
        new LlapDaemonProtocolServerImpl(daemonConf, mock(ContainerRunner.class),
            new AtomicReference<InetSocketAddress>());

    try {
      server.init(new Configuration());
      server.start();
      InetSocketAddress serverAddr = server.getBindAddress();

      LlapDaemonProtocolBlockingPB client =
          new LlapDaemonProtocolClientImpl(new Configuration(), serverAddr.getHostName(),
              serverAddr.getPort());
      client.runContainer(null,
          RunContainerRequestProto.newBuilder().setAmHost("amhost")
              .setAmPort(2000).build());

    } finally {
      server.stop();
    }
  }
}