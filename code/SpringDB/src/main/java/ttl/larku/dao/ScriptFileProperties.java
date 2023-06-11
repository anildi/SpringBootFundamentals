package ttl.larku.dao;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author whynot
 */

@Component
@ConfigurationProperties("ttl.sql")
public class ScriptFileProperties {
    private String schemaFile;
    private String dataFile;

    public String getSchemaFile() {
        return schemaFile;
    }

    public void setSchemaFile(String schemaFile) {
        this.schemaFile = schemaFile;
    }

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    @Override
    public String toString() {
        return "ScriptFileProperties{" +
                "scriptFile='" + schemaFile + '\'' +
                ", dataFile='" + dataFile + '\'' +
                '}';
    }
}
