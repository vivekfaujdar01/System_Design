public class ObjectSocketAdapter implements SocketPlug {
    private final IndianSocket indianSocket;

    public ObjectSocketAdapter(IndianSocket indianSocket) {
        this.indianSocket = indianSocket;
    }

    @Override
    public void provideElectricity() {
        indianSocket.plugInTypeD();
    }
}
