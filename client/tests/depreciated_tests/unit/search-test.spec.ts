import { describe, it, expect, beforeEach, vi } from 'vitest';

import { SharedState } from '../../src/components/SharedState';
// Mocking the global fetch function
global.fetch = vi.fn();

// Utility function to reset mocks between tests
beforeEach(() => {
  vi.clearAllMocks();
});


describe('SearchCommand', () => {
    it('search without loading', async () => {
      // Mock fetch to simulate a successful response
      (fetch as any).mockResolvedValueOnce({
        ok: true,
        json: async () => ({
          result: "error_bad_request",
          message:"No file loaded"
        }),
      });
  
      const sharedState = new SharedState();
      const args = ['1', '|2'];
      const result = await sharedState.SearchCommand(args);
  
      expect(fetch).toHaveBeenCalledTimes(1);
      expect(result).toContain('No file loaded');
    });
  
    it('search with loading', async () => {
      // Mock fetch to simulate a successful response
      (fetch as any).mockResolvedValueOnce({
        ok: true,
        json: async () => ({
          result: "success",
          data: [['hihihi']]
        }),
      });
  
      const sharedState = new SharedState();
      const args = ['hihihi'];
      const result = await sharedState.SearchCommand(args);
  
      expect(fetch).toHaveBeenCalledTimes(1);
      console.log(result);
      expect(result[0]).toContain('hihihi');
    });
    it ('search with no result', async () => {
      // Mock fetch to simulate a successful response
      (fetch as any).mockResolvedValueOnce({
        ok: true,
        json: async () => ({
          result: "success",
          data: []
        }),
      });
  
      const sharedState = new SharedState();
      const args = ['hihihi'];
      const result = await sharedState.SearchCommand(args);
  
      expect(fetch).toHaveBeenCalledTimes(1);
      expect(result).toContain('No match found');
    });
  });