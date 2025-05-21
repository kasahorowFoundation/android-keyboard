#!/usr/bin/env bash

# should support 1.2.3 and 2.3 and 1.2.3-r3 and 1.2beta2 and v1.2.3-r3 and 1.2beta2 and v1.2.3 and v2.3 and v1
export GREP_VERSION_CLASSES="v\\{0,1\\}[[:digit:]]\\{1,\\}[.][[:digit:]]\\{1,\\}[[:graph:]]*"
