# Tokens

Design token values should be inherited from Material library APIs (e.g. `ButtonDefaults`, `CardDefaults`, etc) wherever
possible. Unfortunately, many of the values described in
the [Material component guidelines/specs](https://m3.material.io/components) do not have equivalent values available
from these APIs.

Furthermore, some guidelines are only described visually and/or qualitatively, without specific values.
In these cases, we should define our own tokens for consistent application across all app UIs - easier said than done,
but it's a useful goal.