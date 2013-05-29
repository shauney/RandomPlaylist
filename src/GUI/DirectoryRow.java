package GUI;

public class DirectoryRow {

    private String name = "";
    private String numOfSongs = "0";

    public DirectoryRow(String name, String numOfSongs) {
        this.name = name;
        this.numOfSongs = numOfSongs;
    }

    public Object[] getData() {
        Object[] data = {name, numOfSongs};
        return data;
    }

    public Object getColumn(int columnIndex) {
        return getData()[columnIndex];
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumOfSongs(String numOfSongs) {
        this.numOfSongs = numOfSongs;
    }
}
