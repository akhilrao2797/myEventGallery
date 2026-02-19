import api from './axios';

export const createShareLink = (data) =>
  api.post('/api/shared/create', data).then((r) => r.data);

export const getSharedPublic = (shareCode, password) =>
  api.get(`/api/shared/public/${shareCode}`, {
    params: password ? { password } : {},
  }).then((r) => r.data);

export const myShareLinks = () =>
  api.get('/api/shared/mine').then((r) => r.data);
