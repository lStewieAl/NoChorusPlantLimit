package net.lstewieal.nochoruslimit;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.TransformerExclusions({"net.stewieal.nochoruslimit"})
@IFMLLoadingPlugin.Name("No Chorus Plant Limit Coremod")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class NoChorusLimitPlugin implements IFMLLoadingPlugin
{
    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "net.lstewieal.nochoruslimit.NCLTransformer" };
    }

    @Override
    public String getModContainerClass() {
        return "net.lstewieal.nochoruslimit.NoChorusLimitContainer";
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

}