package com.lightricks.feedexercise.network

data class TemplatesMetadataItem(
    val configuration: String,
    val id: String,
    val isNew: Boolean,
    val isPremium: Boolean,
    val templateCategories: List<String>,
    val templateName: String,
    val templateThumbnailURI: String
)

data class FeedResponse(
    val templatesMetadata: List<TemplatesMetadataItem>
)