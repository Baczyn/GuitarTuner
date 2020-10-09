package com.mikolaj.guitartuner.detection;

import java.util.Arrays;

public class YinEstimator {

    private float[] yinBuffer;

    public YinEstimator(int readSize) {
        this.yinBuffer = new float[readSize/2];
    }

    /**
     * //step 1 - autocorrelation - tunning in next step
     * public void autocorrelation(short[] sample) {
     * <p>
     * for (int t = 0; t < readSize; t++) {
     * for (int j = 1; j < readSize - t; j++) {
     * this.buffer[t] += sample[t] * sample[t + j];
     * }
     * }
     * }
     */
    public float detect(float[] buffer,int sampleRate) {
        final int tauEstimate;
        final float pitchInHertz;
        Arrays.fill(yinBuffer,0);

        differenceFun(buffer);
        cumulativeMeanNormalizedDifference();

        tauEstimate = absoluteThreshold(0.4f);

        float betterTau = parabolicInterpolation(tauEstimate);

        return sampleRate / betterTau;
    }


    //step 2 - Difference function
    public void differenceFun(float[] buffer) {
        for (int tau = 1; tau < yinBuffer.length; tau++)
            for (int i = 0; i < yinBuffer.length; i++)
                yinBuffer[tau] += Math.pow(buffer[i] - buffer[i + tau], 2);
    }

    //step 3 - Cumulative mean normalized difference function
    public void cumulativeMeanNormalizedDifference() {
        yinBuffer[0] = 1;
        float result = 0;
        for (int tau = 1; tau < yinBuffer.length; tau++) {
            result += yinBuffer[tau];
            yinBuffer[tau] *= tau / result;
        }
    }

    //step 4 -  Absolute threshold
    private int absoluteThreshold(float threshold) {
        int tau;
        for (tau = 2; tau < yinBuffer.length; tau++) {
            if (yinBuffer[tau] < threshold) {
                while (tau + 1 < yinBuffer.length && yinBuffer[tau + 1] < yinBuffer[tau]) {
                    tau++;
                }
                break;
            }
        }
        tau = tau >= yinBuffer.length ? yinBuffer.length - 1 : tau;

        return tau;
    }

    //step 5 - Parabolic interpolation
    public float parabolicInterpolation(final int tauEstimate) {
        final float betterTau;
        final int x0;
        final int x2;

        if (tauEstimate < 1) {
            x0 = tauEstimate;
        } else {
            x0 = tauEstimate - 1;
        }
        if (tauEstimate + 1 < yinBuffer.length) {
            x2 = tauEstimate + 1;
        } else {
            x2 = tauEstimate;
        }
        if (x0 == tauEstimate) {
            if (yinBuffer[tauEstimate] <= yinBuffer[x2]) {
                betterTau = tauEstimate;
            } else {
                betterTau = x2;
            }
        } else if (x2 == tauEstimate) {
            if (yinBuffer[tauEstimate-1] <= yinBuffer[x0]) {
                betterTau = tauEstimate;
            } else {
                betterTau = x0;
            }
        } else {
            float s0, s1, s2;
            s0 = yinBuffer[x0];
            s1 = yinBuffer[tauEstimate];
            s2 = yinBuffer[x2];
            // fixed AUBIO implementation, thanks to Karl Helgason:
            // (2.0f * s1 - s2 - s0) was incorrectly multiplied with -1
            betterTau = tauEstimate + (s2 - s0) / (2 * (2 * s1 - s2 - s0));
        }
        return betterTau;
    }



}
