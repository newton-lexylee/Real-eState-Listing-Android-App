package tmedia.ir.melkeurmia.otto;

import com.squareup.otto.Bus;

/**
 * Created by tmedia on 8/20/2017.
 */

public class GlobalBus extends Bus {
    private static Bus sBus;
    public static Bus getBus() {
        if (sBus == null)
            sBus = new Bus();
        return sBus;
    }

}

