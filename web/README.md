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

### Dependency issues

This section keeps track of any dependency issues we are having:

* If we upgrade the `graphql-request` dependency to 6.1.0
  * `@graphql-codegen/typescript-react-query` is not compatible
    * https://github.com/dotansimha/graphql-code-generator-community/issues/367
  * Temporary solution is to keep:
    * `graphql-request` on 4.3.0
    * `@graphql-codegen/typescript-graphql-request` on 4.5.9
      * Newer versions would require a newer version of `graphql-request`
    * `@tanstack/react-query` on 4.36.1
      * Dependency-wise, nothing is stopping us from upgrading, but the resulting generated code is incompatible with newer versions
  * Actual solution
    * Because we don't want to upgrade to react-query v4, we're going to actually not upgrade those dependencies as described in the temporary solution
      * `@graphql-codegen/typescript-react-query` needs to be on a v3.x.x release so that it uses a v3 react-query dependency during codegen. Otherwise there's nothing stopping us from updating this
    * We specifically did not do the temporary solution because it broke `Login.tsx`. The same commit that is writing this is where we changed `Login.tsx` a tiny bit in an attempt to be more compatible with future dependency updates, but it still needs work
