package net.cattaka.hungrycatball.core;

import java.io.IOException;
import java.io.InputStream;

public interface IScreenHandler {
    public void setupMatrix(boolean fromScene, float bottomMargin);

    public InputStream open(String fileName) throws IOException;
}
