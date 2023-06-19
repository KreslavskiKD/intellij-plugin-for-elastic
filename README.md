# intellij-plugin-for-elastic

<!-- ![Build](https://github.com/KreslavskiKD/intellij-plugin-for-elastic/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
-->

<!-- Plugin description -->
## What does it do
Provides a possibility to download logs from Elasticsearch
- To set up preferences such as your Elastic address go to <kbd>File</kbd> -> <kbd>Settings/Preferences</kbd> -> <kbd>Tools</kbd> -> <kbd>Elastic Logs Downloader</kbd>
- Or you can use an action "Set Up Elastic Preferences"
- To set up and run query select an action "Set up and Run Elastic Query" 
You can also just create a blank log scratch file via "Open Log File" action
<!-- Plugin description end -->

## ToDo list
- [ ] [Publish a plugin manually](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate) for the first time.
<!--  - [ ] Set the `PLUGIN_ID` in the above README badges. -->
- [ ] Set the [Plugin Signing](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html?from=IJPluginTemplate) related [secrets](https://github.com/JetBrains/intellij-platform-plugin-template#environment-variables).
- [ ] Set the [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html?from=IJPluginTemplate).

## Installation

- Using IDE built-in plugin system (once it will be deployed):
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "intellij-plugin-for-elastic"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the the code, build it via buildPlugin gradle action and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


