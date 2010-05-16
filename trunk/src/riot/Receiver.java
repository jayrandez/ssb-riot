package riot;

import org.apache.mina.core.session.IoSession;

public interface Receiver {

	void receiveMessage(IoSession sender, byte[] message);
}
