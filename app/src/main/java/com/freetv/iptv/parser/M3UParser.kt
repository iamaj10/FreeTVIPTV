package com.freetv.iptv.parser

import com.freetv.iptv.model.Channel

object M3UParser {

    fun parse(content: String): List<Channel> {

        val channels = mutableListOf<Channel>()

        val lines = content.lines()

        var currentName = ""
        var currentCategory = "Unknown"

        for (i in lines.indices) {

            val line = lines[i].trim()

            if (line.startsWith("#EXTINF")) {

                currentCategory =
                    Regex("""group-title="([^"]*)"""")
                        .find(line)
                        ?.groupValues
                        ?.get(1)
                        ?: "Unknown"

                currentName =
                    line.substringAfterLast(",")
                        .trim()
            }

            else if (
                line.isNotBlank() &&
                !line.startsWith("#")
            ) {

                channels.add(
                    Channel(
                        name = currentName,
                        category = currentCategory,
                        streamUrl = line
                    )
                )
            }
        }

        return channels
    }
}