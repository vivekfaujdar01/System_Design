public class RemoteControl {
    protected final Device device;

    public RemoteControl(Device device) {
        this.device = device;
    }

    public void togglePower() {
        if (device.isEnabled()) {
            device.disable();
            System.out.println(device.getDeviceName() + " is now powered OFF.");
        } else {
            device.enable();
            System.out.println(device.getDeviceName() + " is now powered ON.");
        }
    }

    public void volumeUp() {
        device.setVolume(device.getVolume() + 10);
        System.out.println(device.getDeviceName() + " volume increased to " + device.getVolume() + "%.");
    }

    public void volumeDown() {
        device.setVolume(device.getVolume() - 10);
        System.out.println(device.getDeviceName() + " volume decreased to " + device.getVolume() + "%.");
    }
}
