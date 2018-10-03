/*
 * Copyright (c) 2016 Lung Razvan
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package fragments


import android.app.Activity
import android.content.ActivityNotFoundException
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import billing.BillingFamily
import billing.BillingFamilyAdapter
import billing.BillingPresenter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.draw.LineSeparator
import com.vince.childcare.R
import core.ACTION_UPLOAD_PERMISSION
import core.FileUtils
import core.PermissionUtil
import kotlinx.android.synthetic.main.fragment_billing.view.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException


class Billing : Fragment(), BillingFamilyAdapter.CardItemListener {

  private lateinit var billingFamilyAdapter: BillingFamilyAdapter
  private var billingFamilies: ArrayList<BillingFamily> = ArrayList()
  private lateinit var rv: RecyclerView
  private val billingPresenter = BillingPresenter()
  private lateinit var progress: RelativeLayout

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

    val view: View = inflater.inflate(R.layout.fragment_billing, container, false)
    setupRecyclerView(view)
    billingPresenter.setUp(this, billingFamilies)
    setupBarGraph(view)
    progress = view.progress_layout_billing
    return view
  }

  override fun genBillClicked(position: Int) {
    createPdfWrapper()
  }

  private fun setupBarGraph(view: View) {
    view.billingChart.setDrawBarShadow(false)
    view.billingChart.setDrawValueAboveBar(true)

    val xAxis = view.billingChart.xAxis
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.setDrawGridLines(false)
    xAxis.granularity = 1f // only intervals of 1 day
    xAxis.labelCount = 7

    val leftAxis = view.billingChart.axisLeft
    leftAxis.setLabelCount(8, false)
    leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
    leftAxis.spaceTop = 15f
    leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)

    val rightAxis = view.billingChart.axisRight
    rightAxis.setDrawGridLines(false)
    rightAxis.setLabelCount(8, false)
    rightAxis.spaceTop = 15f
    rightAxis.axisMinimum = 0f // this replaces setStartAtZero(true)

    setData(4, 10f, view.billingChart)
    //todo set real data
  }

  private fun setData(count: Int, range: Float, dashboard_bar_graph: BarChart) {
    //todo set real data

    val start = 1f
    val yVals1 = ArrayList<BarEntry>()

    var i = start.toInt()
    while (i < start + count.toFloat() + 1f) {
      val mult = range + 1
      val `val` = (Math.random() * mult).toFloat()

      if (Math.random() * 100 < 10) {
        yVals1.add(BarEntry(i.toFloat(), `val`, resources.getDrawable(R.drawable.ic_tab_three, null)))
      } else {
        yVals1.add(BarEntry(i.toFloat(), `val`))
      }
      i++
    }

    val set1: BarDataSet

    if (dashboard_bar_graph.data != null && dashboard_bar_graph.data.dataSetCount > 0) {
      set1 = dashboard_bar_graph.data.getDataSetByIndex(0) as BarDataSet
      set1.values = yVals1
      dashboard_bar_graph.data.notifyDataChanged()
      dashboard_bar_graph.notifyDataSetChanged()
    } else {
      set1 = BarDataSet(yVals1, "Registration by Bracket")

      set1.colors = addColors()

      val dataSets = ArrayList<IBarDataSet>()
      dataSets.add(set1)

      val data = BarData(dataSets)
      data.setValueTextSize(10f)
      data.barWidth = 0.9f

      dashboard_bar_graph.data = data
    }
  }

  private fun addColors(): ArrayList<Int> {
    val colors = ArrayList<Int>()

    for (c in ColorTemplate.MATERIAL_COLORS)
      colors.add(c)

    for (c in ColorTemplate.JOYFUL_COLORS)
      colors.add(c)

    for (c in ColorTemplate.COLORFUL_COLORS)
      colors.add(c)

    for (c in ColorTemplate.PASTEL_COLORS)
      colors.add(c)

    for (c in ColorTemplate.LIBERTY_COLORS)
      colors.add(c)

    for (c in ColorTemplate.VORDIPLOM_COLORS)
      colors.add(c)
    return colors
  }

  override fun onResume() {
    super.onResume()
    billingPresenter.getBillingFragmentData(FirebaseAuth.getInstance().currentUser)
  }

  fun updateBilling() {
    billingPresenter.getBillingFragmentData(FirebaseAuth.getInstance().currentUser)
  }

  private fun setupRecyclerView(view: View) {
    rv = view.billing_family_rv as RecyclerView
    rv.layoutManager = LinearLayoutManager(this.context)
    billingFamilyAdapter = BillingFamilyAdapter(this.context!!, billingFamilies, this)
    rv.adapter = billingFamilyAdapter
  }

  fun showProgress() {
    progress.visibility = View.VISIBLE
  }

  fun hideProgress() {
    progress.visibility = View.GONE
  }

  fun refresh(billingFamilyData: ArrayList<BillingFamily>) {
    this.billingFamilies = billingFamilyData
    billingFamilyAdapter.notifyDataSetChanged()
  }


  @Throws(FileNotFoundException::class, DocumentException::class)
  private fun createPdfWrapper() {

    PermissionUtil.handlePermission(this.context as Activity, ACTION_UPLOAD_PERMISSION, PermissionUtil.Permission.WRITE_EXTERNAL_STORAGE)
    if (PermissionUtil.handlePermission(this.context as Activity, ACTION_UPLOAD_PERMISSION, PermissionUtil.Permission.WRITE_EXTERNAL_STORAGE)) {
      val path = File(this.context!!.filesDir, "external_files")
      path.mkdir()
      val file = File(path.path, "test.pdf") //todo change filename
      createPdf(file.path, file)
    }
  }

  private fun createPdf(dest: String, file: File) {

    if (File(dest).exists()) {
      File(dest).delete()
    }

    try {

      val document: Document = FileUtils.createNewPDFDocument(dest) as Document

      // Open to write
      document.open()
      // Document Settings
      setDocumentSettings(document)

      // LINE SEPARATOR
      var lineSeparator = LineSeparator()
      lineSeparator.lineColor = BaseColor(0, 0, 0, 68)

      setHeaderOnDocument(document, lineSeparator, BaseColor(0, 153, 204, 255))
      setBillingDataOnDocument(20.0f, BaseColor(0, 153, 204, 255), document, 16.0f, lineSeparator)

      document.close()
      FileUtils.openFile(this.context as Activity, file)

    } catch (ie: IOException) {
      Log.e("createPdf:", " Error " + ie.localizedMessage)
    } catch (ie: DocumentException) {
      Log.e("createPdf:", " Error " + ie.localizedMessage)
    } catch (ae: ActivityNotFoundException) {
      Toast.makeText(this.context, "No application found to open this file.", Toast.LENGTH_SHORT).show()
    }
  }

  private fun setHeaderOnDocument(document: Document, lineSeparator: LineSeparator, mColorAccent: BaseColor) {
    // Title Order Details...
    // Adding Title....
    val titleFont = Font(Font.FontFamily.COURIER, 18.0f, Font.NORMAL, BaseColor.BLACK)
    val titleChunk = Chunk("Billing Report", titleFont)
    val myImg = FileUtils.getResizedBitmap(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_new), 50, 50)

    document.add(myImg)
    document.add(Paragraph(""))
    document.add(titleChunk)

    // Adding Line Breakable Space....
    document.add(Paragraph(""))
    // Adding Horizontal Line...
    document.add(Chunk(lineSeparator))
    // Adding Line Breakable Space....
    document.add(Paragraph(""))

//    var mOrderIdFont = Font(Font.FontFamily.COURIER, 14.0f, Font.NORMAL, mColorAccent)
    document.add(dataTable())
  }

  private fun dataTable(): PdfPTable {
    var table = PdfPTable(5)

    table.widthPercentage = 100f

    var cell = PdfPCell(Phrase("Child Name Here"))
    cell.horizontalAlignment = Element.ALIGN_CENTER
    cell.verticalAlignment = Element.ALIGN_CENTER
    cell.colspan = 5
    table.addCell(cell)

    var f = Font()
    f.color = BaseColor.BLUE

    table.addCell(PdfPCell(Phrase("Date:", f))).horizontalAlignment = Element.ALIGN_CENTER
    table.addCell(PdfPCell(Phrase("Check-In:", f))).horizontalAlignment = Element.ALIGN_CENTER
    table.addCell(PdfPCell(Phrase("Check-Out:", f))).horizontalAlignment = Element.ALIGN_CENTER
    table.addCell(PdfPCell(Phrase("Time:", f))).horizontalAlignment = Element.ALIGN_CENTER
    table.addCell(PdfPCell(Phrase("Cost:", f))).horizontalAlignment = Element.ALIGN_CENTER
    return table
  }

  private fun setBillingDataOnDocument(mHeadingFontSize: Float, mColorAccent: BaseColor, document: Document,
      mValueFontSize: Float, lineSeparator: LineSeparator) {
    // Fields of Order Details...
    // Adding Chunks for Title and value
//
//    var mOrderIdValueFont = Font(Font.FontFamily.COURIER, mValueFontSize, Font.NORMAL, BaseColor.BLACK)
//    var mOrderIdValueChunk = Chunk("#123123", mOrderIdValueFont)
//    var mOrderIdValueParagraph = Paragraph(mOrderIdValueChunk)
//    document.add(mOrderIdValueParagraph)
//
//    // Adding Line Breakable Space....
//    document.add(Paragraph(""))
//    // Adding Horizontal Line...
//    document.add(Chunk(lineSeparator))
//    // Adding Line Breakable Space....
//    document.add(Paragraph(""))
//
//    // Fields of Order Details...
//    var mOrderDateFont = Font(Font.FontFamily.COURIER, mHeadingFontSize, Font.NORMAL, mColorAccent)
//    var mOrderDateChunk = Chunk("Order Date:", mOrderDateFont)
//    var mOrderDateParagraph = Paragraph(mOrderDateChunk)
//    document.add(mOrderDateParagraph)
//
//    var mOrderDateValueFont = Font(Font.FontFamily.COURIER, mValueFontSize, Font.NORMAL, BaseColor.BLACK)
//    var mOrderDateValueChunk = Chunk("06/07/2017", mOrderDateValueFont)
//    var mOrderDateValueParagraph = Paragraph(mOrderDateValueChunk)
//    document.add(mOrderDateValueParagraph)
//
//    document.add(Paragraph(""))
//    document.add(Chunk(lineSeparator))
//    document.add(Paragraph(""))
//
//    // Fields of Order Details...
//    var mOrderAcNameFont = Font(Font.FontFamily.COURIER, mHeadingFontSize, Font.NORMAL, mColorAccent)
//    var mOrderAcNameChunk = Chunk("Account Name:", mOrderAcNameFont)
//    var mOrderAcNameParagraph = Paragraph(mOrderAcNameChunk)
//    document.add(mOrderAcNameParagraph)
//
//    var mOrderAcNameValueFont = Font(Font.FontFamily.COURIER, mValueFontSize, Font.NORMAL, BaseColor.BLACK)
//    var mOrderAcNameValueChunk = Chunk("Pratik Butani", mOrderAcNameValueFont)
//    var mOrderAcNameValueParagraph = Paragraph(mOrderAcNameValueChunk)
//    document.add(mOrderAcNameValueParagraph)
//
//    document.add(Paragraph(""))
//    document.add(Chunk(lineSeparator))
//    document.add(Paragraph(""))
  }

  private fun setDocumentSettings(document: Document) {
    document.pageSize = PageSize.A4
    document.addCreationDate()
    document.addAuthor(getString(R.string.app_name)) //todo change to user name
    document.addCreator(getString(R.string.app_name))
  }
}
