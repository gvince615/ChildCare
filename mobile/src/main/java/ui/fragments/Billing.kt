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

package ui.fragments


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
import attendance.AttendanceRecord
import billing.BillingChildDataModel
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
import core.*
import kotlinx.android.synthetic.main.fragment_billing.view.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


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
    createPdfWrapper(position)
  }

  private fun setupBarGraph(view: View) {
    view.billingChart.setDrawBarShadow(false)
    view.billingChart.setDrawValueAboveBar(true)

    val xAxis = view.billingChart.xAxis
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.setDrawGridLines(false)
    xAxis.granularity = 2f
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
  private fun createPdfWrapper(position: Int) {

    PermissionUtil.handlePermission(this.context as Activity, ACTION_UPLOAD_PERMISSION, PermissionUtil.Permission.WRITE_EXTERNAL_STORAGE)
    if (PermissionUtil.handlePermission(this.context as Activity, ACTION_UPLOAD_PERMISSION, PermissionUtil.Permission.WRITE_EXTERNAL_STORAGE)) {
      val path = File(this.context!!.filesDir, "external_files")
      path.mkdir()
      val file = File(path.path, billingFamilies[position].familyName + ".pdf") //todo change filename
      createPdf(file.path, file, position)
    }
  }

  private fun createPdf(dest: String, file: File, position: Int) {

    if (File(dest).exists()) {
      File(dest).delete()
    }

    try {

      val document: Document = FileUtils.createNewPDFDocument(dest) as Document

      // Open to write
      document.open()
      // Document Settings
      setDocumentSettings(document)

      setHeaderOnDocument(document, position)
      setBillingDataOnDocument(document, position)

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

  private fun setHeaderOnDocument(document: Document, position: Int) {
    // Title Order Details...
    // Adding Title....
    val titleFont = Font(Font.FontFamily.COURIER, 18.0f, Font.NORMAL, BaseColor.BLACK)
    val titleChunk = Chunk("Billing Report for " + billingFamilies[position].familyName, titleFont)
    val myImg = FileUtils.getResizedBitmap(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_new), 50, 50)

    document.add(myImg)
    document.add(Paragraph(""))
    document.add(titleChunk)

    // Adding Line Breakable Space....
    document.add(Paragraph(""))
  }

  private fun dataTable(child: BillingChildDataModel): PdfPTable {
    val table = PdfPTable(5)
    table.widthPercentage = 100f

    createTableHeader1(table, child)

    createTableHeader(table)
    createTableData(table, child.attendanceRecord)

    return table
  }

  private fun createTableData(table: PdfPTable, records: ArrayList<AttendanceRecord>) {
    val fData = Font()
    fData.color = BaseColor.BLACK

    val dataCell = PdfPCell()
    dataCell.borderColor = BaseColor.WHITE
    dataCell.horizontalAlignment = Element.ALIGN_CENTER

    for (record in records) {

      val dateIn = DateUtil().getDateObjectFromStringFormat(SimpleDateFormat(BILLING_ATTEN_CARD_TIME_FORMAT, Locale.US), record.checkInTime)
      val dateOut = DateUtil().getDateObjectFromStringFormat(SimpleDateFormat(BILLING_ATTEN_CARD_TIME_FORMAT, Locale.US), record.checkOutTime)

      val dateFormatted = DateUtil().getFormattedDateTimeFromDate(SimpleDateFormat(FIRESTORE_MONTH_DAY_FORMAT, Locale.US), dateIn)
      val timeInFormatted = DateUtil().getFormattedDateTimeFromDate(SimpleDateFormat(CHILD_ATTEN_CARD_TIME_FORMAT, Locale.US), dateIn)
      val timeOutFormatted = DateUtil().getFormattedDateTimeFromDate(SimpleDateFormat(CHILD_ATTEN_CARD_TIME_FORMAT, Locale.US), dateOut)

      dataCell.phrase = Phrase(dateFormatted, fData)
      table.addCell(dataCell)

      dataCell.phrase = Phrase(timeInFormatted, fData)
      table.addCell(dataCell)

      dataCell.phrase = Phrase(timeOutFormatted, fData)
      table.addCell(dataCell)

      dataCell.phrase = Phrase(dateIn?.let { dateOut?.let { it1 -> DateUtil().getDateDifference(it, it1) } }, fData)
      table.addCell(dataCell)

      dataCell.phrase = Phrase(" .. ", fData)
      table.addCell(dataCell)
    }
  }


  private fun createTableHeader(table: PdfPTable) {
    val fHeader = Font()
    fHeader.color = BaseColor.BLUE

    table.addCell(PdfPCell(Phrase("Date:", fHeader))).horizontalAlignment = Element.ALIGN_CENTER
    table.addCell(PdfPCell(Phrase("Check-In:", fHeader))).horizontalAlignment = Element.ALIGN_CENTER
    table.addCell(PdfPCell(Phrase("Check-Out:", fHeader))).horizontalAlignment = Element.ALIGN_CENTER
    table.addCell(PdfPCell(Phrase("Time:", fHeader))).horizontalAlignment = Element.ALIGN_CENTER
    table.addCell(PdfPCell(Phrase("Cost:", fHeader))).horizontalAlignment = Element.ALIGN_CENTER
  }

  private fun createTableHeader1(table: PdfPTable, child: BillingChildDataModel) {
    val fHeader1 = Font()
    fHeader1.color = BaseColor.BLACK

    val cellChildName = PdfPCell(Phrase(child.firstName, fHeader1))
    cellChildName.borderColor = BaseColor.WHITE
    cellChildName.colspan = 4
    table.addCell(cellChildName)

    val cellChildId = PdfPCell(Phrase(child.childId, fHeader1))
    cellChildId.borderColor = BaseColor.WHITE
    cellChildId.colspan = 1
    cellChildId.horizontalAlignment = Element.ALIGN_RIGHT
    table.addCell(cellChildId)
  }

  private fun setBillingDataOnDocument(document: Document, position: Int) {
    // Fields of Order Details...
    // Adding Chunks for Title and value
    // LINE SEPARATOR
    var lineSeparator = LineSeparator()
    lineSeparator.lineColor = BaseColor(0, 0, 0, 68)


    for (child in billingFamilies[position].children) {
      document.add(dataTable(child))
      document.add(Paragraph(""))
      document.add(Paragraph(""))
    }

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
