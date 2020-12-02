package io.izzel.taboolib.example;

import io.izzel.taboolib.loader.Plugin;

/**
 * ExamplePlugin
 * io.izzel.taboolib.example.ExamplePlugin
 *
 * @author sky
 * @since 2020/12/2 10:25 下午
 */
public class ExamplePlugin extends Plugin {

    @Override
    public void onEnable() {
        getPlugin().getLogger().info("ExamplePlugin Enabled!");
    }
}
