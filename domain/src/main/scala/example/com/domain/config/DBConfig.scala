package example.com.domain.config

import pureconfig.ConfigReader

case class DBConfig(
    driver: String,
    url: String,
    username: String,
    password: String
) derives ConfigReader
