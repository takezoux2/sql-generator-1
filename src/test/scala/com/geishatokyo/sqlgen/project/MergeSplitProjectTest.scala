package com.geishatokyo.sqlgen.project

import com.geishatokyo.sqlgen.{Context, Executor}
import com.geishatokyo.sqlgen.process.input.XLSFileInput
import com.geishatokyo.sqlgen.process.ensure.EnsureProcessProvider
import com.geishatokyo.sqlgen.process.output.{XLSOutputProvider, SQLOutputProvider}
import com.geishatokyo.sqlgen.process.merge.MergeSplitProcessProvider
import com.geishatokyo.sqlgen.process.{MapContext, Proc}
import org.specs2.mutable.SpecificationWithJUnit

/**
 *
 * User: takeshita
 * Create: 12/07/13 11:47
 */

class MergeSplitProjectTest extends SpecificationWithJUnit {

  "MergeAndSplit executor" should{
    "work" in{
      val e = new MergeSplitExecutorSample
      val wb = e.execute("MergeSplitTest.xls")

      wb("User").column("name").cells.map(_.value) must_== List("test","test2","あああ")
    }
  }

}

class MergeSplitExecutorSample extends Executor[MergeSplitProjectSample]
with XLSFileInput
with EnsureProcessProvider
with MergeSplitProcessProvider
with SQLOutputProvider
with XLSOutputProvider{

  val project = new MergeSplitProjectSample

  val context: Context = new MapContext

  type ProjectType = MergeSplitProjectSample

  protected def executor: Proc = {
    ensureSettingProc then
    mergeAndSplitProc then
    outputSqlProc().skipOnError then
    outputXlsProc().skipOnError
  }
}

class MergeSplitProjectSample extends BaseProject with MergeSplitProject{



  merge sheet "User" from(
    at("Sheet1"){
      column("name")
      column("gender")
    } ,
    at("Sheet2"){
      column("familyName")
    }
    )

  merge sheet "User" select ("name" as "gender") from "Sheet3" where "gender" is "id"

}