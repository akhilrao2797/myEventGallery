import api from './axios';

export const bulkDelete = (eventId, imageIds) =>
  api.post(`/api/events/${eventId}/images/bulk-delete`, { imageIds }).then((r) => r.data);

export const downloadZip = (eventId, imageIds) =>
  api.post(`/api/events/${eventId}/images/download-zip`, { imageIds }, {
    responseType: 'blob',
  }).then((r) => r.data);
