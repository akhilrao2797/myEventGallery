import { getImageUrl } from '../../services/api';

// Mock axios before importing api module
jest.mock('axios', () => {
  const mockAxios = {
    create: jest.fn(() => mockAxios),
    get: jest.fn(),
    post: jest.fn(),
    put: jest.fn(),
    delete: jest.fn(),
    interceptors: {
      request: {
        use: jest.fn(),
        eject: jest.fn(),
      },
      response: {
        use: jest.fn(),
        eject: jest.fn(),
      },
    },
  };
  return mockAxios;
});

describe('API Service', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  describe('Image URL Construction', () => {
    test('getImageUrl returns S3 URL for http URLs', () => {
      const s3Url = 'https://s3.amazonaws.com/bucket/image.jpg';
      const result = getImageUrl(s3Url);
      expect(result).toBe(s3Url);
    });

    test('getImageUrl returns S3 URL for https URLs', () => {
      const s3Url = 'https://s3.amazonaws.com/bucket/image.jpg';
      const result = getImageUrl(s3Url);
      expect(result).toBe(s3Url);
    });

    test('getImageUrl constructs local URL for non-http paths', () => {
      const localPath = 'local-uploads/image.jpg';
      const result = getImageUrl(localPath);
      expect(result).toBe('http://localhost:8080/api/files/local-uploads/image.jpg');
    });

    test('getImageUrl returns empty string for null/undefined', () => {
      expect(getImageUrl(null)).toBe('');
      expect(getImageUrl(undefined)).toBe('');
    });

    test('getImageUrl returns empty string for empty string', () => {
      expect(getImageUrl('')).toBe('');
    });
  });
});
