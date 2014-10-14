/**
 * Copyright 2014  XCL-Charts
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 	
 * @Project XCL-Charts 
 * @Description Android图表基类库
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 * @license http://www.apache.org/licenses/  Apache v2 License
 * @version 1.0
 */

package org.xclcharts.renderer;

import org.xclcharts.chart.PieData;
import org.xclcharts.common.DrawHelper;
import org.xclcharts.common.MathHelper;
import org.xclcharts.renderer.plot.LabelBrokenLine;
import org.xclcharts.renderer.plot.LabelBrokenLineRender;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PointF;
import android.util.Log;

/**
 * @ClassName CirChart
 * @Description 圆形类图表，如饼图，刻度盘...类的图表的基类
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 *  
 */

public class CirChart extends EventChart{
	
	private static final String TAG = "CirChart";
	
	//半径
	private float mRadius=0.0f;		
	
	//标签注释显示位置 [隐藏,Default,Inside,Ouside,Line]
	private XEnum.SliceLabelStyle mLabelStyle  = XEnum.SliceLabelStyle.INSIDE;	
	
	//开放标签画笔让用户设置
	private Paint mPaintLabel = null;
	//初始偏移角度
	protected float mOffsetAngle = 0.0f;//180;	
	
	//平移模式下的可移动方向
	private XEnum.PanMode mPlotPanMode = XEnum.PanMode.FREE;
	private boolean mEnablePanMode = true;
	
	//折线标签基类
	private LabelBrokenLineRender mLabelLine = null;
	
	//同步标签颜色
	private boolean mIsLabelLineSyncColor = false;
	private boolean mIsLabelPointSyncColor = false;
	private boolean mIsLabelSyncColor = false;
		
	public CirChart()
	{		
	}
	
	@Override
	protected void calcPlotRange()
	{
		super.calcPlotRange();		
		
		this.mRadius = Math.min( div(this.plotArea.getWidth() ,2f) , 
				 				 div(this.plotArea.getHeight(),2f) );	
	}
	
	
	/**
	 * 返回半径
	 * @return 半径
	 */
	public float getRadius()
	{
		return mRadius;
	}
	
	/**
	 * 设置饼图(pie chart)起始偏移角度
	 * @param Angle 偏移角度
	 */
	public void setInitialAngle(final int Angle)
	{
		mOffsetAngle = Angle;
	}
	
	/**
	 * 返回图的起始偏移角度
	 * @return 偏移角度
	 */
	public float getInitialAngle()
	{
		return mOffsetAngle;
	}

	/**
	 * 设置标签显示在扇区的哪个位置(里面，外面，隐藏)
	 * @param style 显示位置
	 */
	public void setLabelStyle(XEnum.SliceLabelStyle style)
	{
		mLabelStyle = style;
		//INNER,OUTSIDE,HIDE
		switch(style)
		{
		case INSIDE :
			getLabelPaint().setTextAlign(Align.CENTER);
			break;
		case OUTSIDE :
			break;
		case HIDE :
			break;
		case BROKENLINE:
			break;
		default:			
		}				
	}
	
	/**
	 * 返回标签风格设置
	 * @return	标签风格
	 */
	public XEnum.SliceLabelStyle getLabelStyle()
	{
		return mLabelStyle;
	}
	
	/**
	 * 开放标签画笔
	 * @return 画笔
	 */
	public Paint getLabelPaint()
	{
		if(null == mPaintLabel)
		{
			mPaintLabel = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaintLabel.setColor(Color.BLACK);
			mPaintLabel.setAntiAlias(true);
			mPaintLabel.setTextAlign(Align.CENTER);	
			mPaintLabel.setTextSize(18);
		}
		return mPaintLabel;
	}
	
	/**
	 * 开放折线标签绘制类(当标签为Line类型时有效)
	 * @return 折线标签绘制类
	 */
	public LabelBrokenLine getLabelBrokenLine()
	{
		if(null == mLabelLine)mLabelLine = new LabelBrokenLineRender();
		return mLabelLine;
	}
	
	protected void renderLabelInside(Canvas canvas,String text,float itemAngle,
									 float cirX,float cirY,float radius,float calcAngle)
	{
		//显示在扇形的中心
		float calcRadius = MathHelper.getInstance().sub(radius , radius/2f);
		
		//计算百分比标签
		PointF point = MathHelper.getInstance().calcArcEndPointXY(
										cirX, cirY, calcRadius, calcAngle); 						 
		//标识
		DrawHelper.getInstance().drawRotateText(text, point.x, point.y, itemAngle, 
											canvas, getLabelPaint());
	}
	
	protected void renderLabelOutside(Canvas canvas,String text,float itemAngle,
							float cirX,float cirY,float radius,float calcAngle)
	{
		//显示在扇形的外部
		float calcRadius = MathHelper.getInstance().add(radius  , radius/10f);
		//计算百分比标签
		PointF point = MathHelper.getInstance().calcArcEndPointXY(
										cirX, cirY, calcRadius, calcAngle); 	
			 
		//标识
		DrawHelper.getInstance().drawRotateText(text, point.x, point.y, itemAngle, 
															canvas, getLabelPaint());
	
	}
	
	//折线标签
	protected void renderLabelLine(Canvas canvas,PieData cData,
									float cirX,float cirY,float radius,float calcAngle)
	{		
		if(null == mLabelLine)mLabelLine = new LabelBrokenLineRender();		
		
		if(mIsLabelLineSyncColor)
			mLabelLine.getLabelLinePaint().setColor(cData.getSliceColor());
		if(mIsLabelPointSyncColor)
			mLabelLine.getPointPaint().setColor(cData.getSliceColor());
		
		mLabelLine.renderLabelLine(cData.getLabel(),cData.getItemLabelRotateAngle(),
									cirX,cirY,radius,calcAngle,canvas,getLabelPaint());
	}
	
	/**
	 * 设置标签颜色与当地扇区颜色同步
	 */
	public void syncLabelLineColor()
	{
		mIsLabelLineSyncColor = true;
	}
	
	/**
	 * 设置折线标签点颜色与当地扇区颜色同步
	 */
	public void syncLabelPointColor()
	{
		mIsLabelPointSyncColor = true;
	}
	
	/**
	 * 设置折线标签颜色与当地扇区颜色同步
	 */
	public void syncLabelColor()
	{
		mIsLabelSyncColor = true;
	}
		
	
	/**
	 * 绘制标签
	 * @param cData	PieData类
	 * @param cirX	x坐标
	 * @param cirY	y坐标
	 * @param radius	半径
	 * @param offsetAngle	偏移角度
	 * @param curretAnglet	当前角度
	 */
	protected boolean renderLabel(Canvas canvas, PieData cData,
			final float cirX,
			final float cirY,
			final float radius,		
			final double offsetAngle,
			final double curretAnglet)
	{
		
		
		if(XEnum.SliceLabelStyle.HIDE == mLabelStyle) return true;
		
		String text = cData.getLabel();
		if(""==text||text.length()==0)return true;
				
		float calcAngle = 0.0f;
				
		calcAngle =  (float) MathHelper.getInstance().add(offsetAngle , curretAnglet/2);
		if(Float.compare(calcAngle,0.0f) == 0 
				|| Float.compare(calcAngle,0.0f) == -1 )
		{
			Log.e(TAG,"计算出来的圆心角等于0.");
			return false;
		}
		
		//标签颜色与当地扇区颜色同步
		if(mIsLabelSyncColor) this.getLabelPaint().setColor(cData.getSliceColor());
		
		int color = getLabelPaint().getColor();
				
		//有定制需求
		XEnum.SliceLabelStyle labelStyle = mLabelStyle;
		if( cData.getCustLabelStyleStatus() )
		{
			labelStyle = cData.getLabelStyle();
			if( XEnum.SliceLabelStyle.INSIDE == labelStyle) 
						getLabelPaint().setTextAlign(Align.CENTER);		
			
			getLabelPaint().setColor(cData.getCustLabelColor());
		}
		
		if(XEnum.SliceLabelStyle.INSIDE  == labelStyle)
		{			 
			//显示在扇形的内部
			renderLabelInside(canvas,text,cData.getItemLabelRotateAngle(),
												cirX,cirY,radius,calcAngle);
		}else if(XEnum.SliceLabelStyle.OUTSIDE == labelStyle){
			//显示在扇形的外部
			renderLabelOutside(canvas,text,cData.getItemLabelRotateAngle(),
												cirX,cirY,radius,calcAngle);		
		}else if(XEnum.SliceLabelStyle.BROKENLINE == labelStyle){				
			//显示在扇形的外部
			//1/4处为起始点
			renderLabelLine(canvas,cData,cirX,cirY,radius,calcAngle);
		}else{
			Log.e(TAG,"未知的标签处理类型.");
			return false;
		}		
		
		getLabelPaint().setColor(color);
		return true;
	}
			
	/**
	 * 设置手势平移模式
	 * @param mode	平移模式
	 */
	public void setPlotPanMode(XEnum.PanMode mode)
	{
		 mPlotPanMode = mode;
	}
	
	/**
	 * 返回当前图表平移模式
	 * @return 平移模式
	 */
	public XEnum.PanMode getPlotPanMode()
	{
		return mPlotPanMode;
	}
	
	/**
	 * 激活平移模式
	 */
	public void enablePanMode()
	{
		mEnablePanMode = true;		
	}
	
	/**
	 * 禁用平移模式
	 */
	public void disablePanMode()
	{
		mEnablePanMode = false;		
	}
	
	/**
	 * 返回当前图表的平移状态
	 * @return
	 */
	public boolean getPanModeStatus()
	{
		return mEnablePanMode;
	}
	
		
	@Override
	protected boolean postRender(Canvas canvas) throws Exception 
	{	
		try {						
			super.postRender(canvas);
			
			//计算主图表区范围
			 calcPlotRange();
			//画Plot Area背景			
			 plotArea.render(canvas);			 
			//绘制标题
			renderTitle(canvas);					
		} catch (Exception e) {
			throw e;
		}
		return true;
	}
	
	@Override
	public boolean render(Canvas canvas) throws Exception {
		// TODO Auto-generated method stubcalcPlotRange
		try {
				if (null == canvas)
						return false;
				
				if(getPanModeStatus())
				{											
					canvas.save();
					//设置原点位置					
					switch(this.getPlotPanMode())
					{
					case HORIZONTAL:
						canvas.translate(mTranslateXY[0],0);		
						break;
					case VERTICAL:
						canvas.translate(0,mTranslateXY[1]);		
						break;
					default:
						canvas.translate(mTranslateXY[0],mTranslateXY[1]);
						break;
					}
					
						//绘制图表
						super.render(canvas);
						
					//还原								
					canvas.restore();			
				}else{
					//绘制图表
					super.render(canvas);
				}
						
				return true;				
		} catch (Exception e) {
			throw e;
		}
	}
	
	

}
