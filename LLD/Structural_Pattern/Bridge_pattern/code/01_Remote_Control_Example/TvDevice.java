public class TvDevice implements Device {
    private boolean on = false;
    private int volume = 30;

    @Override public boolean isEnabled() { return on; }
    @Override public void enable() { on = true; }
    @Override public void disable() { on = false; }
    @Override public int getVolume() { return volume; }
    @Override public void setVolume(int percent) { this.volume = percent; }
    @Override public String getDeviceName() { return "TV"; }
}
