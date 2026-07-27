public class HomeTheaterFacade {
    private final DvdPlayer dvd;
    private final Projector projector;
    private final Amplifier amp;
    private final TheaterLights lights;
    private final Screen screen;

    public HomeTheaterFacade(DvdPlayer dvd, Projector projector, Amplifier amp, TheaterLights lights, Screen screen) {
        this.dvd = dvd;
        this.projector = projector;
        this.amp = amp;
        this.lights = lights;
        this.screen = screen;
    }

    public void watchMovie(String movie) {
        System.out.println("=== PREPARING THEATER FOR MOVIE: " + movie + " ===");
        lights.dim(10);
        screen.down();
        projector.on();
        projector.setInput("DVD Player");
        projector.wideScreenMode();
        amp.on();
        amp.setSurroundSound();
        amp.setVolume(25);
        dvd.on();
        dvd.play(movie);
        System.out.println("=== MOVIE IS NOW PLAYING! ENJOY! ===\n");
    }

    public void endMovie() {
        System.out.println("=== SHUTTING DOWN HOME THEATER ===");
        dvd.stop();
        dvd.off();
        amp.off();
        projector.off();
        screen.up();
        lights.on();
        System.out.println("=== THEATER SHUTDOWN COMPLETE ===\n");
    }
}
