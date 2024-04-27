import { describe, it, expect, beforeEach, vi } from 'vitest';
import { SharedState } from '../../src/components/SharedState';

// Mocking the global fetch function
global.fetch = vi.fn();

// Utility function to reset mocks between tests
beforeEach(() => {
  vi.clearAllMocks();
});

describe('BroadbandCommand', () => {
  it('should return broadband metrics on success', async () => {
    // Mock fetch to simulate a successful response
    (fetch as any).mockResolvedValueOnce({
      ok: true,
      json: async () => ({
        result: "success",
        metrics: { 'S2802_C03_022E': '75%' },
        stateName: 'TestState',
        countyName: 'TestCounty'
      }),
    });

    const sharedState = new SharedState();
    const args = ['TestState', 'TestCounty'];
    const result = await sharedState.BroadbandCommand(args);

    expect(fetch).toHaveBeenCalledTimes(1);
    expect(result).toContain('Broadband Percentage: 75%');
    expect(result).toContain('state: TestState');
    expect(result).toContain('county: TestCounty');
  });

  it('should handle fetch failure due to network issues', async () => {
    // Mock fetch to simulate a network failure
    (fetch as any).mockRejectedValueOnce(new Error('Network response was not ok'));

    const sharedState = new SharedState();
    const args = ['TestState', 'TestCounty'];
    const result = await sharedState.BroadbandCommand(args);

    expect(fetch).toHaveBeenCalledTimes(1);
    expect(result).toContain('Error calling broadband:');
  });
});