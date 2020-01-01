# Jackson JSON Library
This project uses the Jackson JSON library because Jackson has many features that makes developing
a lot quicker. The biggest reason why we use this is annotations. Jackson has many annotations to provide
all the possible options for configuration that this project needs.

### Explicit
The code uses a custom annotation: `JsonExplicit`. This annotation stops Jackson from auto detecting all
public methods, fields, etc. Instead of configuring this using an `ObjectMapper`, this project heavily relies
on annotations. The reason behind this is that we want classes to work with an `ObjectMapper` with little to no configuration.
