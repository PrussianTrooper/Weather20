package naillibip.firstapp.weather.event;

public class UpdateRecyclerList {
    private int position;

    public UpdateRecyclerList(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
