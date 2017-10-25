# User-Service

This service provides the version for a given contextPath and a given user.

```
1.2.3   -> only version 1.2.3
1.2.3-SNAPSHOT -> only specified version

1.2.3*  -> version 1.2.3 + all of 1.2.3-SNAPSHOT
^1.2.3  -> 1.2.3 + all versions greater but none of 1.2.3-SNAPSHOT
^1.2.3* -> 1.2.3 + 1.2.3-SNAPSHOT + all versions greater
```

the "*" should not be used in PROD