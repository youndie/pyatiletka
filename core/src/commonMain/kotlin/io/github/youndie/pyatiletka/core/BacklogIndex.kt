package io.github.youndie.pyatiletka.core

/**
 * Reads the generated index of a docs-bootstrap backlog — the block between `<!-- BEGIN INDEX -->`
 * and `<!-- END INDEX -->` in `backlog.md` — into the plan's counters and its unclosed items, without
 * opening a single item file (research §1.4). The marks are docs-bootstrap's `MARK`: `[ ]` open,
 * `[~]` wip, `[x]` done, `[?]` question, `[-]` dropped.
 *
 * Open items are rows of a table, `| [B-NN](path) `[ ]` | title | priority | size | blocked by |`;
 * closed ones are lines, `- [B-NN](path) `[x]` - title`, under stage headings. Anything else in the
 * block is ignored. Returns null when the markers are not there: then the file is not an index, and
 * the repository's plan is not counted.
 *
 * [itemUrl] turns an item's path, as the index links it, into a link a reader can open.
 */
fun parseBacklogIndex(
    markdown: String,
    itemUrl: (path: String) -> String = { it },
): Plan? {
    val begin = markdown.indexOf(BEGIN)
    val end = markdown.indexOf(END)
    if (begin < 0 || end < begin) return null
    var done = 0
    var open = 0
    var wip = 0
    var question = 0
    var dropped = 0
    val items = mutableListOf<BacklogItem>()
    markdown.substring(begin + BEGIN.length, end).lineSequence().forEach { line ->
        val row = OPEN_ROW.matchEntire(line.trim())
        if (row != null) {
            val (id, path, mark, title, priority) = row.destructured
            val status =
                when (mark) {
                    "?" -> ItemStatus.Question
                    "~" -> ItemStatus.Wip
                    "x", "-" -> null
                    else -> ItemStatus.Open
                }
            when (mark) {
                "?" -> question++
                "~" -> wip++
                "x" -> done++
                "-" -> dropped++
                else -> open++
            }
            if (status != null) {
                items +=
                    BacklogItem(
                        id,
                        title.trim(),
                        status,
                        priority.trim().takeIf { it.isNotEmpty() && it != "-" },
                        itemUrl(path),
                    )
            }
            return@forEach
        }
        val closed = CLOSED_LINE.matchEntire(line.trim()) ?: return@forEach
        when (closed.groupValues[1]) {
            "x" -> done++
            "-" -> dropped++
            // A closed section holds only the two closing marks; anything else there is not counted.
        }
    }
    return Plan(PlanForm.Items, PlanCounts(done, open, wip, question, dropped), items)
}

private const val BEGIN = "<!-- BEGIN INDEX -->"
private const val END = "<!-- END INDEX -->"

// The title is everything up to the last three cells; titles may hold anything but a line break.
private val OPEN_ROW = Regex("""^\|\s*\[(B-\d+)]\(([^)]*)\)\s*`\[(.)]`\s*\|(.*)\|([^|]*)\|[^|]*\|[^|]*\|$""")
private val CLOSED_LINE = Regex("""^- \[B-\d+]\([^)]*\)\s*`\[(.)]`.*$""")
