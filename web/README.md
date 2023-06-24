## web
This is a gradle submodule, and this README contains technical details about this module.
This module is used for the web application bundled with the `graphql` program.

### Play nicely with IntelliJ
If you have a local NPM install, run these commands:

```shell
ln -s "$(which node)" /usr/local/bin/node
ln -s "$(which npm)" /usr/local/bin/npm
```

### Info
This expects >= Node 16. NPM is used instead of yarn, which is why there is no `yarn.lock` file.

### How this was created
This was created using `npx create-react-app web --template typescript` in the solarthing (root) directory.

To make IntelliJ play nice with a nvm install of npm, see https://stackoverflow.com/a/66730709/5434860.
