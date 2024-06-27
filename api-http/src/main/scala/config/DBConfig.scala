package com.example.api.http

import pureconfig.ConfigReader
import pureconfig.generic.derivation.default.*

case class DBConfig(
    driver: String,
    url: String,
    username: String,
    password: String
) derives ConfigReader
