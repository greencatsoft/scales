package scales.binding

import scala.util.Success

import com.greencatsoft.greenlight.TestSuite

object ConverterSupportTest extends TestSuite with ConverterSupport {

  "ConverterSupport" should "return a ConverterNotFoundException when the requested type is not supported" in {
    def convert(value: String)(implicit converter: Converter[Exception]) = converter(value)

    A_[ConverterNotFoundException] should be_thrown_in {
      convert("abc").get
    }
  }

  "ConverterSupport.intConverter" should "provide an implicit conversion from a string literal to an Int value" in {
    def convert(value: String)(implicit converter: Converter[Int]) = converter(value)

    val result = convert("123")

    result.isSuccess should be (true)
    result foreach {
      _ should be (123)
    }
  }

  It should "return an instance of ConversionFailureException when it is given an invalid input data" in {
    def convert(value: String)(implicit converter: Converter[Int]) = converter(value)

    A_[ConversionFailureException] should be_thrown_in {
      convert("abc").get
    }
  }

  "ConverterSupport.floatConverter" should "provide an implicit conversion from a string literal to a Float value" in {
    def convert(value: String)(implicit converter: Converter[Float]) = converter(value)

    val result = convert("123f")

    result.isSuccess should be (true)
    result foreach {
      _ should be (123f)
    }
  }

  It should "return an instance of ConversionFailureException when it is given an invalid input data" in {
    def convert(value: String)(implicit converter: Converter[Float]) = converter(value)

    A_[ConversionFailureException] should be_thrown_in {
      convert("abc").get
    }
  }

  "ConverterSupport.longConverter" should "provide an implicit conversion from a string literal to a Long value" in {
    def convert(value: String)(implicit converter: Converter[Long]) = converter(value)

    val result = convert("1234123412341234")

    result.isSuccess should be (true)
    result foreach {
      _ should be (1234123412341234L)
    }
  }

  It should "return an instance of ConversionFailureException when it is given an invalid input data" in {
    def convert(value: String)(implicit converter: Converter[Long]) = converter(value)

    A_[ConversionFailureException] should be_thrown_in {
      convert("abc").get
    }
  }

  "ConverterSupport.doubleConverter" should "provide an implicit conversion from a string literal to a Double value" in {
    def convert(value: String)(implicit converter: Converter[Double]) = converter(value)

    val result = convert("1234123412341234d")

    result.isSuccess should be (true)
    result foreach {
      _ should be (1234123412341234d)
    }
  }

  It should "return an instance of ConversionFailureException when it is given an invalid input data" in {
    def convert(value: String)(implicit converter: Converter[Double]) = converter(value)

    A_[ConversionFailureException] should be_thrown_in {
      convert("abc").get
    }
  }

  "ConverterSupport.booleanConverter" should "provide an implicit conversion from a string literal to a Boolean value" in {
    def convert(value: String)(implicit converter: Converter[Boolean]) = converter(value)

    val result = convert("true")

    result.isSuccess should be (true)
    result foreach {
      _ should be (true)
    }
  }

  It should "return an instance of ConversionFailureException when it is given an invalid input data" in {
    def convert(value: String)(implicit converter: Converter[Boolean]) = converter(value)

    A_[ConversionFailureException] should be_thrown_in {
      convert("abc").get
    }
  }

  "ConverterSupport.stringConverter" should "return the given argument as it is" in {
    def convert(value: String)(implicit converter: Converter[String]) = converter(value)

    val result = convert("abc")

    result.isSuccess should be (true)
    result foreach {
      _ should be ("abc")
    }
  }

  "ConverterSupport.optionConverter" should "provide implicit conversion between a string value and an optional value of the specified type" in {
    def convert[T](value: String)(implicit converter: Converter[Option[T]]) = converter(value)

    convert[Int]("123") should be (Success(Some(123)))
    convert[Float]("123f") should be (Success(Some(123f)))
    convert[Long]("1234123412341234") should be (Success(Some(1234123412341234L)))
    convert[Double]("1234123412341234d") should be (Success(Some(1234123412341234d)))
    convert[Boolean]("true") should be (Success(Some(true)))
    convert[String]("abc") should be (Success(Some("abc")))
  }

  It should "return an instance of ConversionFailureException when it is given an invalid input data" in {
    def convert[T](value: String)(implicit converter: Converter[Option[T]]) = converter(value)

    A_[ConversionFailureException] should be_thrown_in {
      convert[Int]("abc").get
    }

    A_[ConversionFailureException] should be_thrown_in {
      convert[Float]("abc").get
    }

    A_[ConversionFailureException] should be_thrown_in {
      convert[Long]("abc").get
    }

    A_[ConversionFailureException] should be_thrown_in {
      convert[Double]("abc").get
    }

    A_[ConversionFailureException] should be_thrown_in {
      convert[Boolean]("abc").get
    }
  }

  It should "return Success(None) when the given argument is either empty or a null value" in {
    def convert[T](value: String)(implicit converter: Converter[Option[T]]) = converter(value)

    convert[Int]("") should be (Success(None))
    convert[Int](null) should be (Success(None))
    convert[Float]("") should be (Success(None))
    convert[Float](null) should be (Success(None))
    convert[Long]("") should be (Success(None))
    convert[Long](null) should be (Success(None))
    convert[Double]("") should be (Success(None))
    convert[Double](null) should be (Success(None))
    convert[Boolean]("") should be (Success(None))
    convert[Boolean](null) should be (Success(None))
    convert[String]("") should be (Success(None))
    convert[String](null) should be (Success(None))
  }
}