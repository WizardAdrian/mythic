package mythic.adrian.imageprocessor.render.handler;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Adrian on 2017/8/4.
 * E-mail:aliu@in66.com
 */

public abstract class DrawHandler<Output> implements ISurfaceAction{

    protected DrawHandler successor;

    public abstract Output handleDraw(GL10 gl10);

    public DrawHandler getSuccessor() {
        return successor;
    }

    public void setSuccessor(DrawHandler successor) {
        this.successor = successor;
    }
}
