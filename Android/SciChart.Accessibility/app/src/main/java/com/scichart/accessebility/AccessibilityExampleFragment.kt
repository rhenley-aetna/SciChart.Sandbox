package com.scichart.accessebility

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.fragment.app.Fragment
import com.scichart.accessebility.databinding.AccessibilityExampleFragmentBinding
import com.scichart.accessebility.helpers.AxisNode
import com.scichart.accessebility.helpers.ColumnPointNode
import com.scichart.accessebility.helpers.INode
import com.scichart.accessebility.modifiers.AccessiblePinchZoomModifier
import com.scichart.accessebility.modifiers.AccessibleZoomExtentsModifier
import com.scichart.accessebility.modifiers.AccessibleZoomPanModifier
import com.scichart.charting.model.dataSeries.IXyDataSeries
import com.scichart.charting.model.dataSeries.XyDataSeries
import com.scichart.charting.modifiers.ModifierGroup
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries
import com.scichart.core.framework.UpdateSuspender
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.common.LinearGradientBrushStyle
import com.scichart.drawing.common.SolidPenStyle
import com.scichart.drawing.utility.ColorUtil
import com.scichart.extensions.builders.SciChartBuilder
import java.util.*
import kotlin.random.Random

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class AccessibilityExampleFragment : Fragment() {

    private lateinit var binding: AccessibilityExampleFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AccessibilityExampleFragmentBinding.inflate(inflater, container, false)

        initExample(binding)

        return binding.root
    }

//    override fun onResume() {
//        super.onResume()
//        initExample(binding)
//        binding.refreshButton.setOnClickListener { b ->
//             TODO: clear the surface and re-run initExample()
//        }
//    }

    private fun initExample(binding: AccessibilityExampleFragmentBinding) {
        SciChartBuilder.init(context);
        val sciChartBuilder: SciChartBuilder = SciChartBuilder.instance()

        // Original sample's Axis definition code.
//        val xAxis: NumericAxis = NumericAxis(context).apply {
//            growBy = DoubleRange(0.1, 0.1)
//        }
//        val yAxis: NumericAxis = NumericAxis(context).apply {
//            growBy = DoubleRange(0.0, 0.1)
//        }
        val xAxis: NumericAxis = sciChartBuilder
            .newNumericAxis()
            .withAxisTitle("X Axis Title")
            .withGrowBy(0.1, 0.1)
            .build()
        val yAxis: NumericAxis = sciChartBuilder
            .newNumericAxis()
            .withAxisTitle("Y Axis Title")
            .withGrowBy(0.0, 0.1)
            .build()

        val surface: AccessibleSciChartSurface = binding.surface
        val nodes: ArrayList<INode> = surface.helper.nodes

        // Original sample's XYDataSeries declaration.
//        val ds: IXyDataSeries<Int, Int> = XyDataSeries<Int, Int>(
//            Int::class.javaObjectType,
//            Int::class.javaObjectType
//        ).apply {
//            seriesName = "Column chart"
//        }
        val ds: IXyDataSeries<Int, Int> = sciChartBuilder.newXyDataSeries<Int, Int>(
            Int::class.javaObjectType,
            Int::class.javaObjectType
        )
            .withSeriesName("Column chart")
            .build()

        // Second data series experiment.
//        val ds2: IXyDataSeries<Int, Int> = sciChartBuilder.newXyDataSeries<Int, Int>(
//            Int::class.javaObjectType,
//            Int::class.javaObjectType
//        )
//            .withSeriesName("Other chart")
//            .build()

        val yValues = intArrayOf(50, 35, 61, 58, 50, 50, 40, 53, 55, 23, 45, 12, 59, 60)

        for (i in yValues.indices) {
            val yValue = yValues[i]
            ds.append(i, yValue)

            // Second data series experiment - Interleave the nodes for the two series:
            // TalkBack reads each point in the second series immediately after the first series point with the same x-value
            // (Note: Can't select the second series columns by touch -- must flick to them in order.
            // Issue is with ColumnPointNode selection bounds, which don't know about the other column,
            // but the issue is fixable by putting the lower valued bar first in a11y node order,
            // so its hit test is run first.)
//            val yValue2 = yValue - Random.nextInt(10)
//            ds2.append(i, yValue2)
//            // insert the shorter node first...
//            nodes.add(ColumnPointNode(yValues.size + i, i.toDouble(), yValue2.toDouble(), surface))

            nodes.add(ColumnPointNode(i, i.toDouble(), yValue.toDouble(), surface))
        }
        // Second data series experiment - Alternative sequence:
        // Add nodes for the second series after all of the nodes for the first series:
        // TalkBack reads the second series points in order after all of the first series points.
        // (Note: Can't select the second series columns by touch -- must flick to them in order.
        // Issue is with ColumnPointNode selection bounds, which don't know about the other column.)
//        for (i in yValues.indices) {
//            val yValue2 = yValues[i] - Random.nextInt(10)
//            ds2.append(i, yValue2)
//
//            nodes.add(ColumnPointNode(yValues.size + i, i.toDouble(), yValue2.toDouble(), surface))
//        }

        val rSeries: FastColumnRenderableSeries = sciChartBuilder.newColumnSeries()
            .withStrokeStyle(ColorUtil.White, 1f, true)
            .withDataPointWidth(0.7)
            .withLinearGradientColors(ColorUtil.LightSteelBlue, ColorUtil.SteelBlue)
            .withDataSeries(ds)
            .build()

        // Original sample's ColumnSeries definition.
        // Note use of 1f.toDip() which gives same results as .withStrokeColor(..., 1f, ...)
//        val rSeries: FastColumnRenderableSeries = FastColumnRenderableSeries().apply {
//            strokeStyle = SolidPenStyle(ColorUtil.White, true, 1f.toDip(), null)
//            dataPointWidth = 0.7
//            fillBrushStyle = LinearGradientBrushStyle(
//                0f,
//                0f,
//                1f,
//                1f,
//                ColorUtil.LightSteelBlue,
//                ColorUtil.SteelBlue
//            )
//            dataSeries = ds
//        }

        // Second data series experiment.
//        val r2Series: FastColumnRenderableSeries = sciChartBuilder.newColumnSeries()
//            .withStrokeStyle(ColorUtil.White, 1f, true)
//            .withDataPointWidth(0.7)
//            .withLinearGradientColors(ColorUtil.GreenYellow, ColorUtil.DarkOliveGreen)
//            .withDataSeries(ds2)
//            .build()

        UpdateSuspender.using(surface) {
            surface.xAxes.add(xAxis)
            surface.yAxes.add(yAxis)

            surface.renderableSeries.add(rSeries)
            // Second data series experiment.
//            surface.renderableSeries.add(r2Series)

            val pinchZoomModifier = AccessiblePinchZoomModifier()
            val zoomPanModifier = AccessibleZoomPanModifier().apply {
                receiveHandledEvents = true
            }
            val zoomExtentsModifier = AccessibleZoomExtentsModifier()
            surface.chartModifiers.add(
                ModifierGroup(pinchZoomModifier, zoomPanModifier, zoomExtentsModifier)
            )

            surface.theme =
                if (requireContext().isDarkThemeOn()) R.style.SciChart_SciChartv4DarkStyle else R.style.SciChart_Bright_Spark
        }

        val xAxisNode = AxisNode(xAxis, nodes.size)
        nodes.add(xAxisNode)

        val yAxisNode = AxisNode(yAxis, nodes.size)
        nodes.add(yAxisNode)

        xAxis.setVisibleRangeChangeListener { axis, oldRange, newRange, isAnimating -> // need to send this even to update position of rects on screen during scrolling
            surface.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)
        }

        yAxis.setVisibleRangeChangeListener { axis, oldRange, newRange, isAnimating -> // need to send this even to update position of rects on screen during scrolling
            surface.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)
        }
    }

    // Adjust for current displayMetrics; replaced by use of SciChartBuilder API.
    fun Float.toDip(): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            Resources.getSystem().displayMetrics
        )
    }

    fun Context.isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
    }
}