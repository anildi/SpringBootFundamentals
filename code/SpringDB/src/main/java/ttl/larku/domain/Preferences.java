package ttl.larku.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Preferences {

    private boolean professional;

    //Checkbox
    private String[] interests;

    //Checkboxes
    private String[] favoriteNotes;
    private String[] notesList = {"A", "Bb", "C#", "Eb", "G"};

    //Radio Button
    private String preferredDirection;

    //Select with items
    private String stringInstrument;
    private String[] stringInstruments = {"Banjo", "Kora", "Double Bass", "Guitar", "Sarod", "Oud"};

    //Select with options
    private String windInstrument;
    private Map<String, String> windInstruments;

    //Text Area
    private String deepestThoughts;

    public Preferences() {
        windInstruments = new HashMap<String, String>();
        windInstruments.put("Bone", "Trombone");
        windInstruments.put("LowAndSweet", "Clarinet");
        windInstruments.put("Wedding Singer", "Shehnai");
        windInstruments.put("NoteKeeper", "Oboe");
        windInstruments.put("Aerophone", "Bagpipe");
    }


    public String getPreferredDirection() {
        return preferredDirection;
    }

    public void setPreferredDirection(String preferredDirection) {
        this.preferredDirection = preferredDirection;
    }

    public boolean isProfessional() {
        return professional;
    }

    public void setProfessional(boolean professional) {
        this.professional = professional;
    }

    public String[] getInterests() {
        return interests;
    }

    public void setInterests(String[] interests) {
        this.interests = interests;
    }

    public String[] getFavoriteNotes() {
        return favoriteNotes;
    }

    public void setFavoriteNotes(String[] favoriteNotes) {
        this.favoriteNotes = favoriteNotes;
    }

    public String[] getNotesList() {
        return notesList;
    }

    public void setNotesList(String[] notesList) {
        this.notesList = notesList;
    }

    public String getStringInstrument() {
        return stringInstrument;
    }

    public void setStringInstrument(String instrument) {
        this.stringInstrument = instrument;
    }

    public String[] getStringInstruments() {
        return stringInstruments;
    }

    public void setStringInstruments(String[] instruments) {
        this.stringInstruments = instruments;
    }

    public String getWindInstrument() {
        return windInstrument;
    }

    public void setWindInstrument(String windInstrument) {
        this.windInstrument = windInstrument;
    }

    public Map<String, String> getWindInstruments() {
        return windInstruments;
    }

    public void setWindInstruments(Map<String, String> windInstruments) {
        this.windInstruments = windInstruments;
    }

    public String getDeepestThoughts() {
        return deepestThoughts;
    }


    public void setDeepestThoughts(String deepestThoughts) {
        this.deepestThoughts = deepestThoughts;
    }


    @Override
    public String toString() {
        return "Preferences [professional=" + professional + ", interests="
                + Arrays.toString(interests) + ", favoriteNotes="
                + Arrays.toString(favoriteNotes) + ", notesList="
                + Arrays.toString(notesList) + ", preferredDirection="
                + preferredDirection + ", stringInstrument=" + stringInstrument
                + ", stringInstruments=" + Arrays.toString(stringInstruments)
                + ", windInstrument=" + windInstrument + ", windInstruments="
                + windInstruments + ", deepestThoughts=" + deepestThoughts
                + "]";
    }


}