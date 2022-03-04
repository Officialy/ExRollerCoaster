package erc.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import erc.core.ERCConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;

/**
 * .obj file loader for the coasters and sushi
 */
public class ModelLoader implements IModelLoader {
//    public static final ExampleModel EX_MO = new ExampleModel();


    public boolean accepts(ResourceLocation modelLocation) {
        String domain = modelLocation.getPath();//todo .getResourceDomain();
        if(domain.equals(ERCConstants.DOMAIN) || domain.equals(ERCConstants.D_AM)) {
            String path = modelLocation.getPath();
            return path.startsWith("coaster") || path.startsWith("sushi");
        }
        return false;
    }

    @Override
    public IModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        //return EX_MO;
        return null;
    }

    @Override
    public void onResourceManagerReload(ResourceManager p_10758_) {

    }
}