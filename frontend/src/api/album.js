import api from './axios';

export const generateAlbumPdf = (eventId, imageIds) =>
  api.post('/api/album/generate', { eventId, imageIds }, {
    responseType: 'blob',
  }).then((r) => r.data);
