package ttl.larku.sql;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author whynot
 */

@Component
@ConfigurationProperties("spring.sql.init")
public class ScriptFileProperties {
    private String schemaLocations;
    private List<String> allSchemaLocs = new ArrayList<>();
    private List<String> allDataLocs = new ArrayList<>();
    private String dataLocations;

    public String getSchemaLocations() {
        return schemaLocations;
    }

    public void setSchemaLocations(String schemaLocations) {
        allSchemaLocs.clear();
        this.schemaLocations = schemaLocations;
        var arr = schemaLocations.split(",\\s*");
        for(var s : arr) {
            allSchemaLocs.add(s.trim());
        }
    }

    public List<String> getAllSchemaLocs() {
        return allSchemaLocs;
    }

    public String getDataLocations() {
        return dataLocations;
    }

    public void setDataLocations(String dataLocations) {
        this.dataLocations = dataLocations;
        allDataLocs.clear();
        var arr = dataLocations.split(",\\s*");
        for(var s : arr) {
            allDataLocs.add(s.trim());
        }
    }

    public List<String> getAllDataLocs() {
        return allDataLocs;
    }

    @Override
    public String toString() {
        return "ScriptFileProperties{" +
                "scriptFile='" + schemaLocations + '\'' +
                ", dataFile='" + dataLocations + '\'' +
                '}';
    }
}
