package main

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

type albumInfo struct {
	Artist string `json:"artist"`
	Title  string `json:"title"`
	Year   string `json:"year"`
}

type imageMetaData struct {
	AlbumID   string `json:"albumID"`
	ImageSize string `json:"imageSize"`
}

func main() {
	router := gin.Default()
	router.GET("/albums/:id", getAlbumByID)
	router.POST("/albums", postAlbums)

	router.Run(":1234")
}

// postAlbums adds an album from JSON received in the request body.
func postAlbums(c *gin.Context) {
	var fixedImageMetaData imageMetaData
	fixedImageMetaData.AlbumID = "fixed-album-ID"
	fixedImageMetaData.ImageSize = "fixed-image-size"

	// Add the new album to the slice.
	// albums = append(albums, newImageMetaData)
	c.IndentedJSON(http.StatusCreated, fixedImageMetaData)
}

// getAlbumByID locates the album whose ID value matches the id
// parameter sent by the client, then returns that album as a response.
func getAlbumByID(c *gin.Context) {
	var fixedAlbumInfo albumInfo
	fixedAlbumInfo.Artist = "Sex Pistols"
	fixedAlbumInfo.Title = "Never Mind The Bollocks!"
	fixedAlbumInfo.Year = "1977"

	c.IndentedJSON(http.StatusOK, fixedAlbumInfo)
}
