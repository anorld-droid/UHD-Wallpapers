package com.anorlddroid.wallpapers4e.data

import androidx.compose.runtime.Immutable

@Immutable
data class Categories(
    val name: String,
    val downloads: String,
    val imageUrl: String
)

val categories = listOf(
    Categories(
        "Animals",
        "50,000+",
        "https://images.unsplash.com/photo-1568265112889-c9d3fc50a281"
    ),
    Categories(
        "Architecture",
        "5,800+",
        "https://images.unsplash.com/photo-1567449303055-2cd2ef5c4e74"
    ),
    Categories(
        "Arts & Crafts",
        "12100+",
        "https://images.unsplash.com/photo-1506806732259-39c2d0268443"
    ),
    Categories(
        "Business",
        "78+",
        "https://images.unsplash.com/photo-1614224362433-4800a95e45c7"
    ),
    Categories(
        "Fashion",
        "9200+",
        "https://images.unsplash.com/photo-1492707892479-7bc8d5a4ee93"

    ),
    Categories(
        "Lifestyle",
        "30,500",
        "https://images.unsplash.com/photo-1616593918824-4fef16054381"
    ),
    Categories(
        "Music",
        "21,200+",
        "https://images.unsplash.com/photo-1511379938547-c1f69419868d"
    ),
    Categories(
        "Nature",
        "37549+",
        "https://images.unsplash.com/photo-1469474968028-56623f02e42e"
    ),
    Categories(
        "Photography",
        "321+",
        "https://images.unsplash.com/photo-1516035069371-29a1b244cc32"
    ),
    Categories(
        "Sports",
        "13,489+",
        "https://images.unsplash.com/photo-1587280501635-68a0e82cd5ff"
    ),
    Categories(
        "Travel",
        "19,541+",
        "https://images.unsplash.com/photo-1488085061387-422e29b40080"
    ),
    Categories(
        "Technology",
        "11008+",
        "https://images.unsplash.com/photo-1488590528505-98d2b5aba04b"
    )
)
