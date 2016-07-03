package assets;

import graphics.Attribute;
import graphics.Usage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by germangb on 03/07/16.
 */
public class MeshHints {

    public Usage usage = Usage.STATIC;
    public Map<Attribute, Usage> bufferUsage = new HashMap<>();
}
