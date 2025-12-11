
# MoodTunes


## Description

MoodTunes is a mobile app that creates personalized playlists based on your mood, instantly matching your feelings with the perfect music.

The APK is at the root of the project and is named ***MoodTunes.apk***

## Usage
You must have a .env file at the root of the backend folder that look like this:
```
GEMINI_API_KEY=your_gemini_api_key
SPOTIFY_CLIENT_ID=your_spotify_id
SPOTIFY_CLIENT_SECRET=your_spotify_client_secred
SPOTIFY_REDIRECT_URI=moodtunes://callback ## let this like that
```

To launch the backend:
```sh
cd backend/
docker compose up --build
```

The swagger is at this address: **http://localhost:8080/swagger/index.html**


## Co-Contributors

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/Chaegnal">
        <img src="https://avatars.githubusercontent.com/u/114568162?v=4" width=85><br>
        <sub>Eva Legrand</sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/Igoatyouu">
        <img src="https://avatars.githubusercontent.com/u/84337619?v=4" width=85><br>
        <sub>Axel Tasheau</sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/edward-lalande">
        <img src="https://avatars.githubusercontent.com/u/114470214?v=4" width=85><br>
        <sub>Edward Lalande</sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/NicolasMelet">
        <img src="https://avatars.githubusercontent.com/u/114576883?v=4" width=85><br>
        <sub>Nicolas Melet</sub>
      </a>
    </td>
  </tr>
</table>
