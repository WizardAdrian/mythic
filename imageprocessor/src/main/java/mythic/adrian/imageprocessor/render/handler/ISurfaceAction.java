package mythic.adrian.imageprocessor.render.handler;

/**
 * Created by Adrian on 2017/8/4.
 * E-mail:aliu@in66.com
 */

public interface ISurfaceAction<Env, Config> {

    void createAction(Env env, Config config);

    void changeAction(Env env, int width, int height);

    void destroyAction();
}
