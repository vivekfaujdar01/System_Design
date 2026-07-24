public class Main {
    public static void main(String[] args) {
        System.out.println("=== Testing Basic Remote with TV ===");
        Device tv = new TvDevice();
        RemoteControl basicRemote = new RemoteControl(tv);
        basicRemote.togglePower();
        basicRemote.volumeUp();

        System.out.println("\n=== Testing Advanced Remote with Radio ===");
        Device radio = new RadioDevice();
        AdvancedRemoteControl advancedRemote = new AdvancedRemoteControl(radio);
        advancedRemote.togglePower();
        advancedRemote.volumeUp();
        advancedRemote.mute();
    }
}
