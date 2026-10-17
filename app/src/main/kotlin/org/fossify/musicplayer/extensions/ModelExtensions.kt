package org.fossify.musicplayer.extensions

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import org.fossify.commons.extensions.getFilenameFromPath
import org.fossify.commons.extensions.getFormattedDuration
import org.fossify.commons.helpers.AlphanumericComparator
import org.fossify.commons.helpers.SORT_DESCENDING
import org.fossify.musicplayer.helpers.*
import org.fossify.musicplayer.models.*
import java.io.File

// Track helpers
fun Track.Companion.getComparator(sorting: Int) = Comparator<Track> { first, second ->
    var result = when {
        (sorting and PLAYER_SORT_BY_TITLE) != 0 -> {
            when {
                first.title == MediaStore.UNKNOWN_STRING && second.title != MediaStore.UNKNOWN_STRING -> 1
                first.title != MediaStore.UNKNOWN_STRING && second.title == MediaStore.UNKNOWN_STRING -> -1
                else -> AlphanumericComparator().compare(first.title.lowercase(), second.title.lowercase())
            }
        }

        (sorting and PLAYER_SORT_BY_ARTIST_TITLE) != 0 -> {
            when {
                first.artist == MediaStore.UNKNOWN_STRING && second.artist != MediaStore.UNKNOWN_STRING -> 1
                first.artist != MediaStore.UNKNOWN_STRING && second.artist == MediaStore.UNKNOWN_STRING -> -1
                else -> AlphanumericComparator().compare(first.artist.lowercase(), second.artist.lowercase())
            }
        }

        (sorting and PLAYER_SORT_BY_TRACK_ID) != 0 -> {
            val discComparison = (first.discNumber ?: Int.MAX_VALUE).compareTo(second.discNumber ?: Int.MAX_VALUE)
            if (discComparison == 0) {
                first.trackId?.compareTo(second.trackId ?: 0) ?: 0
            } else {
                discComparison
            }
        }

        (sorting and PLAYER_SORT_BY_DATE_ADDED) != 0 -> first.dateAdded.compareTo(second.dateAdded)
        (sorting and PLAYER_SORT_BY_CUSTOM) != 0 -> first.orderInPlaylist.compareTo(second.orderInPlaylist)
        else -> first.duration.compareTo(second.duration)
    }

    if ((sorting and SORT_DESCENDING) != 0) {
        result *= -1
    }

    return@Comparator result
}

fun Track.getBubbleText(sorting: Int) = when {
    (sorting and PLAYER_SORT_BY_TITLE) != 0 -> title
    (sorting and PLAYER_SORT_BY_ARTIST_TITLE) != 0 -> artist
    else -> duration.getFormattedDuration()
}

fun Track.getProperTitle(showFilename: Int): String {
    return when (showFilename) {
        SHOW_FILENAME_NEVER -> title
        SHOW_FILENAME_IF_UNAVAILABLE -> if (title == MediaStore.UNKNOWN_STRING) path.getFilenameFromPath() else title
        else -> path.getFilenameFromPath()
    }
}

fun Track.getUri(): Uri = if (mediaStoreId == 0L || (flags and FLAG_MANUAL_CACHE) != 0) {
    Uri.fromFile(File(path))
} else {
    ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mediaStoreId)
}

fun Track.isCurrent() = (flags and FLAG_IS_CURRENT) != 0

// Playlist helpers
fun Playlist.Companion.getComparator(sorting: Int) = Comparator<Playlist> { first, second ->
    var result = when {
        (sorting and PLAYER_SORT_BY_TITLE) != 0 -> AlphanumericComparator().compare(first.title.lowercase(), second.title.lowercase())
        else -> first.trackCount.compareTo(second.trackCount)
    }

    if ((sorting and SORT_DESCENDING) != 0) {
        result *= -1
    }

    return@Comparator result
}

fun Playlist.getBubbleText(sorting: Int) = when {
    (sorting and PLAYER_SORT_BY_TITLE) != 0 -> title
    else -> trackCount.toString()
}

// Artist helpers
fun Artist.Companion.getComparator(sorting: Int) = Comparator<Artist> { first, second ->
    var result = when {
        (sorting and PLAYER_SORT_BY_TITLE) != 0 -> {
            when {
                first.title == MediaStore.UNKNOWN_STRING && second.title != MediaStore.UNKNOWN_STRING -> 1
                first.title != MediaStore.UNKNOWN_STRING && second.title == MediaStore.UNKNOWN_STRING -> -1
                else -> AlphanumericComparator().compare(first.title.lowercase(), second.title.lowercase())
            }
        }

        (sorting and PLAYER_SORT_BY_ALBUM_COUNT) != 0 -> first.albumCnt.compareTo(second.albumCnt)
        else -> first.trackCnt.compareTo(second.trackCnt)
    }

    if ((sorting and SORT_DESCENDING) != 0) {
        result *= -1
    }

    return@Comparator result
}

fun Artist.getBubbleText(sorting: Int) = when {
    (sorting and PLAYER_SORT_BY_TITLE) != 0 -> title
    (sorting and PLAYER_SORT_BY_ALBUM_COUNT) != 0 -> albumCnt.toString()
    else -> trackCnt.toString()
}

// Album helpers
fun Album.Companion.getComparator(sorting: Int) = Comparator<Album> { first, second ->
    var result = when {
        (sorting and PLAYER_SORT_BY_TITLE) != 0 -> {
            when {
                first.title == MediaStore.UNKNOWN_STRING && second.title != MediaStore.UNKNOWN_STRING -> 1
                first.title != MediaStore.UNKNOWN_STRING && second.title == MediaStore.UNKNOWN_STRING -> -1
                else -> AlphanumericComparator().compare(first.title.lowercase(), second.title.lowercase())
            }
        }

        (sorting and PLAYER_SORT_BY_ARTIST_TITLE) != 0 -> {
            when {
                first.artist == MediaStore.UNKNOWN_STRING && second.artist != MediaStore.UNKNOWN_STRING -> 1
                first.artist != MediaStore.UNKNOWN_STRING && second.artist == MediaStore.UNKNOWN_STRING -> -1
                else -> AlphanumericComparator().compare(first.artist.lowercase(), second.artist.lowercase())
            }
        }

        (sorting and PLAYER_SORT_BY_DATE_ADDED) != 0 -> first.dateAdded.compareTo(second.dateAdded)
        else -> first.year.compareTo(second.year)
    }

    if ((sorting and SORT_DESCENDING) != 0) {
        result *= -1
    }

    return@Comparator result
}

fun Album.getBubbleText(sorting: Int) = when {
    (sorting and PLAYER_SORT_BY_TITLE) != 0 -> title
    (sorting and PLAYER_SORT_BY_ARTIST_TITLE) != 0 -> artist
    else -> year.toString()
}

// Genre helpers
fun Genre.Companion.getComparator(sorting: Int) = Comparator<Genre> { first, second ->
    var result = when {
        (sorting and PLAYER_SORT_BY_TITLE) != 0 -> AlphanumericComparator().compare(first.title.lowercase(), second.title.lowercase())
        else -> first.trackCnt.compareTo(second.trackCnt)
    }

    if ((sorting and SORT_DESCENDING) != 0) {
        result *= -1
    }

    return@Comparator result
}

fun Genre.getBubbleText(sorting: Int) = when {
    (sorting and PLAYER_SORT_BY_TITLE) != 0 -> title
    else -> trackCnt.toString()
}
