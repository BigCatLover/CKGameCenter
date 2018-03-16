
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class RadarChartRenderer extends LineScatterCandleRadarRenderer {

    protected RadarChart mChart;
    protected Paint mCirclePaintInner;
    protected List<Entry> entries;
    protected RadarDataSet dataSet;

    /** paint for drawing the web */
    protected Paint mWebPaint;

    public RadarChartRenderer(RadarChart chart, ChartAnimator animator,
            ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.STROKE);
        mHighlightPaint.setStrokeWidth(2f);
        mHighlightPaint.setColor(Color.rgb(255, 187, 115));

        mWebPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWebPaint.setStyle(Paint.Style.STROKE);

        mCirclePaintInner = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaintInner.setStyle(Paint.Style.FILL);
        mCirclePaintInner.setColor(Color.WHITE);
    }

    public Paint getWebPaint() {
        return mWebPaint;
    }

    @Override
    public void initBuffers() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void drawData(Canvas c) {

        RadarData radarData = mChart.getData();

        for (RadarDataSet set : radarData.getDataSets()) {

            if (set.isVisible())
                drawDataSet(c, set);
        }
    }

    protected void drawDataSet(Canvas c, RadarDataSet dataSet) {
        this.dataSet = dataSet;
        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        PointF center = mChart.getCenterOffsets();

        entries = dataSet.getYVals();

        Path surface = new Path();

        boolean hasMovedToPoint = false;

        for (int j = 0; j < entries.size(); j++) {

            mRenderPaint.setColor(dataSet.getColor(j));

            Entry e = entries.get(j);

            PointF p = Utils.getPosition(center, (e.getVal()*mAnimator.getPhaseY() - mChart.getYChartMin()) * factor,
                    sliceangle * j + mChart.getRotationAngle());

            if (Float.isNaN(p.x))
                continue;

            if (!hasMovedToPoint) {
                surface.moveTo(p.x, p.y);
                hasMovedToPoint = true;
            } else
                surface.lineTo(p.x, p.y);
        }

        surface.close();

        // draw filled
        if (dataSet.isDrawFilledEnabled()) {
            mRenderPaint.setStyle(Paint.Style.FILL);
            mRenderPaint.setAlpha(dataSet.getFillAlpha());
            c.drawPath(surface, mRenderPaint);
            mRenderPaint.setAlpha(255);
        }

        mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
        mRenderPaint.setStyle(Paint.Style.STROKE);

        // draw the line (only if filled is disabled or alpha is below 255)
        if (!dataSet.isDrawFilledEnabled() || dataSet.getFillAlpha() < 255)
            c.drawPath(surface, mRenderPaint);
    }

    @Override
    public void drawValues(Canvas c) {

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        PointF center = mChart.getCenterOffsets();

        for (int i = 0; i < mChart.getData().getDataSetCount(); i++) {

            RadarDataSet dataSet = mChart.getData().getDataSetByIndex(i);

            if (!dataSet.isDrawValuesEnabled())
                continue;

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            List<Entry> entries = dataSet.getYVals();
//            mValuePaint.setStyle(Paint.Style.FILL);
            for (int j = 0; j < entries.size(); j++) {
                Entry e = entries.get(j);
                mCirclePaintInner.setColor(mChart.getValueColorInner());
                PointF p = Utils.getPosition(center, (e.getVal()*mAnimator.getPhaseY() - mChart.getYChartMin()) * factor,
                        sliceangle * j + mChart.getRotationAngle());

                c.drawCircle(p.x, p.y, 1f, mCirclePaintInner);

            }
        }
    }

    @Override
    public void drawExtras(Canvas c) {
        drawWeb(c);
    }

    protected void drawWeb(Canvas c) {

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();
        float rotationangle = mChart.getRotationAngle();

        PointF center = mChart.getCenterOffsets();

        // draw the web lines that come from the center
        mWebPaint.setStrokeWidth(mChart.getWebLineWidth());
        mWebPaint.setColor(mChart.getWebColor());

        for (int i = 0; i < mChart.getData().getXValCount(); i++) {

            PointF p = Utils.getPosition(center, mChart.getYRange() * factor, sliceangle * i
                    + rotationangle);
            c.drawLine(center.x, center.y, p.x, p.y, mWebPaint);
        }

        // draw the inner-web

        mWebPaint.setColor(mChart.getWebColorInner());

        int labelCount = mChart.getYAxis().mEntryCount;
        for (int j = 1; j < labelCount-1; j+=2) {
            mWebPaint.setStrokeWidth(mChart.getWebLineWidthInner());
            for (int i = 0; i < mChart.getData().getXValCount(); i++) {

                float r = (mChart.getYAxis().mEntries[j] - mChart.getYChartMin()) * factor;

                PointF p1 = Utils.getPosition(center, r, sliceangle * i + rotationangle);
                PointF p2 = Utils.getPosition(center, r, sliceangle * (i + 1) + rotationangle);

                c.drawLine(p1.x, p1.y, p2.x, p2.y, mWebPaint);
            }
        }

        int k = labelCount-2;
        Path surface = new Path();

        boolean hasMovedToPoint = false;

        for (int j = 0; j < entries.size(); j++) {

            mRenderPaint.setColor(mChart.getBgColorInner());
            mWebPaint.setAlpha(mChart.getWebAlpha());
            Entry e = entries.get(j);

            PointF p = Utils.getPosition(center, (e.getVal()*mAnimator.getPhaseY() - mChart.getYChartMin()) * factor,
                    sliceangle * j + mChart.getRotationAngle());

            if (Float.isNaN(p.x))
                continue;

            if (!hasMovedToPoint) {
                surface.moveTo(p.x, p.y);
                hasMovedToPoint = true;
            } else
                surface.lineTo(p.x, p.y);
        }
        PointF p1 =null;
        if(k<0){
            return;
        }
        for (int i = 0; i < mChart.getData().getXValCount(); i++) {

            float r = (mChart.getYAxis().mEntries[k] - mChart.getYChartMin()) * factor;
            PointF p2 = Utils.getPosition(center, r, sliceangle * i + rotationangle);
            if(i==0){
                p1 = p2;
            }
            surface.lineTo(p2.x, p2.y);
        }
        surface.lineTo(p1.x, p1.y);
        surface.close();

        // draw filled
        if (dataSet.isDrawFilledEnabled()) {
            mRenderPaint.setStyle(Paint.Style.FILL);
            mRenderPaint.setAlpha(dataSet.getFillAlpha());
            c.drawPath(surface, mRenderPaint);
            mRenderPaint.setAlpha(255);
        }

    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        float sliceangle = mChart.getSliceAngle();
        float factor = mChart.getFactor();

        PointF center = mChart.getCenterOffsets();

        for (int i = 0; i < indices.length; i++) {

            RadarDataSet set = mChart.getData()
                    .getDataSetByIndex(indices[i]
                            .getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            mHighlightPaint.setColor(set.getHighLightColor());
            mHighlightPaint.setStrokeWidth(set.getHighlightLineWidth());

            // get the index to highlight
            int xIndex = indices[i].getXIndex();

            Entry e = set.getEntryForXIndex(xIndex);
            if (e == null || e.getXIndex() != xIndex)
                continue;

            int j = set.getEntryPosition(e);
            float y = (e.getVal()*mAnimator.getPhaseY() - mChart.getYChartMin());

            if (Float.isNaN(y))
                continue;

            PointF p = Utils.getPosition(center, y * factor,
                    sliceangle * j + mChart.getRotationAngle());

            float[] pts = new float[] {
                    p.x, 0, p.x, mViewPortHandler.getChartHeight(), 0, p.y,
                    mViewPortHandler.getChartWidth(), p.y
            };

            // draw the lines
            drawHighlightLines(c, pts, set.isHorizontalHighlightIndicatorEnabled(), set.isVerticalHighlightIndicatorEnabled());
        }
    }

}
