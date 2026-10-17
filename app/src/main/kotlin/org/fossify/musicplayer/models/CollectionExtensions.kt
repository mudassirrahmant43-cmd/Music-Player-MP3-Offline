package org.fossify.musicplayer.models

import androidx.media3.common.MediaItem
import org.fossify.musicplayer.extensions.sortSafely
import org.fossify.musicplayer.extensions.toMediaItem

@JvmName("sortTracksSafely")
fun ArrayList<Track>.sortSafely(sorting: Int) = sortSafely(Track.getComparator(sorting))

fun Collection<Track>.toMediaItems() = map { it.toMediaItem() }

fun Collection<Track>.toMediaItemsFast() = map {
    MediaItem.Builder()
        .setMediaId(it.mediaStoreId.toString())
        .build()
}

@JvmName("sortPlaylistsSafely")
fun ArrayList<Playlist>.sortSafely(sorting: Int) = sortSafely(Playlist.getComparator(sorting))

@JvmName("sortArtistsSafely")
fun ArrayList<Artist>.sortSafely(sorting: Int) = sortSafely(Artist.getComparator(sorting))

@JvmName("sortAlbumsSafely")
fun ArrayList<Album>.sortSafely(sorting: Int) = sortSafely(Album.getComparator(sorting))

@JvmName("sortGenresSafely")
fun ArrayList<Genre>.sortSafely(sorting: Int) = sortSafely(Genre.getComparator(sorting))
